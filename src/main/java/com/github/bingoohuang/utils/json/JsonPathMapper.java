package com.github.bingoohuang.utils.json;

import com.github.bingoohuang.utils.filter.Filters;
import com.github.bingoohuang.utils.reflect.Fields;
import com.github.bingoohuang.utils.str.Tmpl;
import com.github.bingoohuang.utils.type.Generic;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jooq.lambda.Seq;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class JsonPathMapper {
    private static Map<String, BiFunction<Object, List<String>, Object>> predefinedFilters = Maps.newHashMap();

    static {
        initFilters();
    }

    @SuppressWarnings("unchecked")
    static void initFilters() {
        registerFilter("max", (s, args) -> s instanceof List ? Seq.seq((List) s).max().orElseGet(null) : s);
        registerFilter("min", (s, args) -> s instanceof List ? Seq.seq((List) s).min().orElseGet(null) : s);
    }

    public static void registerFilter(String name, BiFunction<Object, List<String>, Object> func) {
        predefinedFilters.put(name, func);
    }

    public static Object filter(Object value, String filtersOptions) {
        return Filters.filter(predefinedFilters, value, filtersOptions);
    }

    private DocumentContext root;
    private final Map<String, String> variables = Maps.newHashMap();

    public JsonPathMapper(Object json) {
        this.root = JsonPath.parse(json);
    }

    public JsonPathMapper(String json) {
        this.root = JsonPath.parse(json);
    }

    public <T> List<T> mapToList(Class<T> beanClass) {
        List<T> list = Lists.newArrayList();

        val jsonPathing = beanClass.getAnnotation(JsonPathing.class);
        if (jsonPathing != null) {
            val read = evalJsonPath(jsonPathing.value()[0]);
            if (read instanceof JSONArray) {
                val array = (JSONArray) read;
                for (val element : array) {
                    list.add(new JsonPathMapper(element).mapInternal(beanClass));
                }
            } else if (read != null) {
                list.add(new JsonPathMapper(read).mapInternal(beanClass));
            }
        }

        return list;
    }

    public <T> T map(Class<T> beanClass) {
        val jsonPathing = beanClass.getAnnotation(JsonPathing.class);
        if (jsonPathing == null) return mapInternal(beanClass);

        val read = evalJsonPath(jsonPathing.value()[0]);

        if (read == null) return null;

        if (read instanceof JSONArray) {
            val array = (JSONArray) read;
            return array.isEmpty() ? null
                    : new JsonPathMapper(array.get(0)).mapInternal(beanClass);
        }

        return new JsonPathMapper(read).mapInternal(beanClass);
    }

    @SuppressWarnings("unchecked") private <T> T mapInternal(Class<T> beanClass) {
        T bean = new ObjenesisStd().getInstantiatorOf(beanClass).newInstance();
        for (val field : beanClass.getDeclaredFields()) {
            if (Fields.shouldIgnored(field)) continue;

            processField(bean, field);
        }

        return bean;
    }

    private Object evalJsonPath(String jsonPath) {
        try {
            val read = root.read(jsonPath);
            log.debug("eval JsonPath {} with result {} ", jsonPath, read);
            return read;
        } catch (PathNotFoundException ex) {
            log.warn("JsonPath {} not found", jsonPath);
            return null;
        } catch (Exception ex) {
            log.warn("eval JsonPath {} fail", jsonPath, ex);
            return null;
        }
    }

    @SneakyThrows
    private <T> void processField(T bean, Field field) {
        val jsonPathing = field.getAnnotation(JsonPathing.class);
        if (jsonPathing == null) return;

        Fields.setAccessible(field);

        Object value = evalJsonPath(jsonPathing);
        if (value == null) return;

        val genericType = new Generic(field.getGenericType());
        if (genericType.isRawType(List.class)) {
            val list = processList(value, genericType.getActualTypeArg(0));
            field.set(bean, list);
        } else {
            value = processCatchExpr(jsonPathing, value);
            value = filter(value, jsonPathing.filter());
            if (value instanceof JSONArray && ((JSONArray) value).size() == 1) {
                value = ((JSONArray) value).get(0);
            }

            saveVariables(field, value);
            value = processMapping(jsonPathing, value);

            setFieldValue(field, bean, value);
        }
    }

    private Object processMapping(JsonPathing jsonPathing, Object value) {
        if (StringUtils.isEmpty(jsonPathing.mapping())) return value;

        val mapped = mapping(jsonPathing.mapping(), "" + value);
        log.debug("mapped {} to {}", value, mapped);

        return mapped;
    }

    private void saveVariables(Field field, Object value) {
        if (value instanceof String) {
            variables.put(field.getName(), (String) value);
        }
    }

    @SuppressWarnings("unchecked")
    private Object processCatchExpr(JsonPathing jsonPathing, Object value) {
        if (StringUtils.isEmpty(jsonPathing.catchExpr())) return value;

        val pattern = Pattern.compile(jsonPathing.catchExpr());

        if (value instanceof List) {
            return Seq.seq((List) value).map(x -> processCatchExpr(jsonPathing.catchExpr(), pattern, x)).toList();
        }

        return processCatchExpr(jsonPathing.catchExpr(), pattern, value);
    }

    private Object processCatchExpr(String catchExpr, Pattern pattern, Object value) {
        val matcher = pattern.matcher("" + value);
        if (matcher.find()) {
            val group = matcher.group();
            log.debug("match expr {} found {}", catchExpr, group);
            return group;
        }

        log.debug("match expr {} not found", catchExpr);
        return value;
    }

    private List processList(Object value, Class<?> itemClass) {
        val list = Lists.newArrayList();

        if (!(value instanceof JSONArray)) return list;

        val a = (JSONArray) value;
        if (a.isEmpty()) return list;

        if (a.size() == 1 && a.get(0) instanceof JSONArray) {
            ((JSONArray) a.get(0)).forEach(x -> list.add(new JsonPathMapper(x).map(itemClass)));
        } else {
            a.forEach(x -> list.add(new JsonPathMapper(x).map(itemClass)));
        }

        return list;
    }

    private String mapping(String mappingExpr, String value) {
        val parts = Splitter.on(',').omitEmptyStrings().trimResults().split(mappingExpr);
        val partArray = Iterables.toArray(parts, String.class);

        for (int i = 0; i < partArray.length; i += 2) {
            val target = partArray[i];
            if (i + 1 >= partArray.length) return target;
            if (StringUtils.equals(target, value)) return partArray[i + 1];
        }

        return value;
    }

    private Object evalJsonPath(JsonPathing jsonPathing) {
        String[] jsonPaths = jsonPathing.value();
        for (int i = 0; i < jsonPaths.length; ++i) {
            jsonPaths[i] = Tmpl.eval(jsonPaths[i], variables);
        }

        if (jsonPaths.length == 1) {
            return evalJsonPath(jsonPaths[0]);
        } else if (jsonPaths.length > 1) {
            return Seq.of(jsonPaths).map(x -> "" + evalJsonPath(x)).toString();
        }

        return null;
    }

    @SneakyThrows
    private void setFieldValue(Field field, Object bean, Object fieldValue) {
        if (field.getType() == DateTime.class) {
            fieldValue = new DateTime(Long.parseLong((String) fieldValue));
        }

        if (fieldValue instanceof JSONArray) {
            val array = (JSONArray) fieldValue;
            if (!array.isEmpty()) fieldValue = array.get(0);
        }

        field.set(bean, fieldValue);
    }
}
