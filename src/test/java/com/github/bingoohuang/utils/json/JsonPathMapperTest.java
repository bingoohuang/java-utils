package com.github.bingoohuang.utils.json;

import com.github.bingoohuang.utils.lang.Classpath;
import lombok.val;
import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class JsonPathMapperTest {
    @Test
    public void test() {
        // https://jsoneditoronline.org/#/
        val json = Classpath.loadResAsString("json/sf.json");
        val candidate = new JsonPathMapper(json).map(SfCandidate.class);
        assertThat(candidate.toString()).isEqualTo("SfCandidate(name=胡经纬中, candidateId=202, gender=男, " +
                "birth=" + new DateTime(631152000000L) + ", cellPhone=18811448245, " +
                "email=827794655@qq.com, edu=硕士研究生, major=0/660/410620/410624, workStartDate=" + new DateTime(1383264000000L) + ", " +
                "jobApplies=[" +
                "SfCandidate.SfJobApply(source=猎聘网(集团总部), positionNumber=80036777, jobTitle=Prod大易测试), " +
                "SfCandidate.SfJobApply(source=hanergy064, positionNumber=20005985, jobTitle=高级电气工程师)])");

        val candidateList = new JsonPathMapper(json).mapToList(SfCandidate.class);
        assertThat(candidateList).containsExactly(candidate);
    }

    @Test
    public void empty() {
        val json = Classpath.loadResAsString("json/sf-empty.json");
        val candidate = new JsonPathMapper(json).map(SfCandidate.class);
        assertThat(candidate).isNull();
    }


    @Test
    public void multi() {
        val json = Classpath.loadResAsString("json/sf-multi.json");
        val candidateList = new JsonPathMapper(json).mapToList(SfCandidate.class);
        assertThat(candidateList.toString()).isEqualTo("[" +
                "SfCandidate(name=胡经纬中, candidateId=202, gender=男, birth=" + new DateTime(631152000000L) + ", " +
                "cellPhone=18811448245, email=827794655@qq.com, edu=硕士研究生, major=0/660/410620/410624, workStartDate=" + new DateTime(1383264000000L) + ", " +
                "jobApplies=[" +
                "SfCandidate.SfJobApply(source=猎聘网(集团总部), positionNumber=80036777, jobTitle=Prod大易测试), " +
                "SfCandidate.SfJobApply(source=hanergy064, positionNumber=20005985, jobTitle=高级电气工程师)]), " +
                "" +
                "SfCandidate(name=周丽强, candidateId=201, gender=男, birth=" + new DateTime(252460800000L) + ", " +
                "cellPhone=13636558850, email=1347382158@qq.com, edu=大学本科, major=0/660/695/412193, workStartDate=" + new DateTime(1104537600000L) + ", " +
                "jobApplies=[" +
                "SfCandidate.SfJobApply(source=猎聘网(集团总部), positionNumber=80036777, jobTitle=Prod大易测试), " +
                "SfCandidate.SfJobApply(source=襄阳越顶管理咨询有限公司-詹成龙, positionNumber=20028898, jobTitle=工艺高级工程师)])]");
    }

}