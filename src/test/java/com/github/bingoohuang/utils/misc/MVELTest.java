package com.github.bingoohuang.utils.misc;

import com.github.bingoohuang.utils.lang.Classpath;
import com.github.bingoohuang.utils.proxy.ReadOnlyMap;
import lombok.val;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class MVELTest {
    @Test
    public void interpret() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);
        val result = (Boolean) MVEL.eval("foobar > 99", vars);
        if (result) {
            System.out.println("It works!");
        }
    }

    @Test
    public void activityId() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("activityId", "3872149");
        assertThat((Boolean) MVEL.eval("activityId == '387249'", vars)).isFalse();
        vars.put("activityId", "387249");
        assertThat((Boolean) MVEL.eval("activityId == '387249'", vars)).isTrue();

        vars.put("马脸", "1.1");
        vars.put("李咏", "2.2");
        vars.put("挂了", "3.3");

        assertThat(MVEL.evalToString("((float)李咏+(float)挂了+(float)马脸)/3", vars))
                .isEqualTo("2.2");
    }

    @Test
    public void compile() {
        val compiled = MVEL.compileExpression("foobar > 99");
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);
        val result = (Boolean) MVEL.executeExpression(compiled, vars);
        if (result) {
            System.out.println("It works!");
        }
    }


    @Test @SuppressWarnings("unchecked")
    public void processBuilder() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("foobar", 100);

        val proxy = ReadOnlyMap.proxy(vars::get);

        val resolver = new MapVariableResolverFactory(proxy);
        val script = (ExecutableStatement) MVEL.compileExpression("foobar > 99");
        val result = (Boolean) script.getValue(null, resolver);
        System.out.println("result:" + result);
    }

    @Test
    public void parse() {
        val pctx = ParserContext.create();
        MVEL.compileExpression("基本素质 + Math.sqrt(核心素质)", pctx);
        System.out.println(pctx.getInputs()); // {foobar=class java.lang.Object}
    }

    @Test
    public void usageOfPropertyExpression() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee", new Employee("john", "michale"));
        String lastName = MVEL.evalToString("employee.lastName", input);
        assertThat(lastName).isEqualTo("michale");

        Boolean yes = MVEL.evalToBoolean("employee.lastName == \"john\"", input);
        assertThat(yes).isFalse();

        input.put("numeric", -0.253405);
        Boolean no = MVEL.evalToBoolean("numeric > 0", input);
        assertThat(no).isFalse();
    }

    @Test
    public void testMVELController() {
        Map<String, Object> input = new HashMap<>();
        input.put("employee", new Employee("john", "michale"));

        String lastName = MVEL.evalToString("employee.lastName", input);
        assertThat(lastName).isEqualTo("michale");

        assertThat(MVEL.evalToBoolean("employee.lastName == \"john\"", input)).isFalse();

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("john", "michale"));
        employees.add(new Employee("merlin", "michale"));
        input.put("employees", employees);
        @SuppressWarnings("unchecked")
        val eval = (List<Employee>) MVEL.eval("($ in employees if $.firstName == \"john\")", input);
        assertThat(eval).containsExactly(new Employee("john", "michale"));

        assertThat(MVEL.evalToString(Classpath.loadResAsString("concat.mvel"), input))
                .isEqualTo("[johnmichale, merlinmichale]");
    }

    @Test
    public void MVELTemplateController() {
        // Usecase1: Injecting the dynamic property to the static HTML content.
        // MVEL supports the decision making tags to place the default values in case of the actual property value is null
        // Input map should contain the key name otherwise it will throw the exception
        String message = "<html>Hello @if{userName!=null && userName!=''}@{userName}@else{}Guest@end{}! Welcome to MVEL tutorial<html>";
        System.out.println("Input Expression:" + message);
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("userName", "Blog Visitor");
        System.out.println("InputMap:" + inputMap);
        String compliedMessage = applyTemplate(message, inputMap);
        System.out.println("compliedMessage:" + compliedMessage);

        // Usecase4: Forming dynamic query by binding the dynamic values
        // We can build complex queries by using the decision making tags@if,@else and for loop tags @for
        // We can bind the values from the bean to expression
        String queryExpression = "select * from @{schemaName}.@{tableName} where @{condition}";
        Map<String, Object> queryInput = new HashMap<>();
        queryInput.put("schemaName", "testDB");
        queryInput.put("tableName", "employee");
        queryInput.put("condition", "age > 25 && age < 30");
        String query = applyTemplate(queryExpression, queryInput);
        System.out.println("Dynamic Query:" + query);

        // Usecase5: Forming dynamic API calls
        String weatherAPI = "http://api.openweathermap.org/data/2.5/weather?lat=@{latitude}&lon=@{longitude}";
        Map<String, Object> apiInput = new HashMap<>();
        apiInput.put("latitude", "35");
        apiInput.put("longitude", "139");
        String weatherAPICall = applyTemplate(weatherAPI, apiInput);
        System.out.println("weatherAPICall:" + weatherAPICall);


        val s = MVEL.evalToString("'lat=' + latitude + ' ' + '^lon=' + longitude", apiInput);
        System.out.println(s);
    }

    /**
     * Method used to bind the values to the MVEL syntax and return the complete expression to understand by any other engine.
     */
    public static String applyTemplate(String expression, Map<String, Object> parameterMap) {
        val compliedTemplate = TemplateCompiler.compileTemplate(expression);
        return (String) TemplateRuntime.execute(compliedTemplate, parameterMap);
    }
}
