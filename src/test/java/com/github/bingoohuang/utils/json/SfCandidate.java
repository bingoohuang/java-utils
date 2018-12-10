package com.github.bingoohuang.utils.json;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.joda.time.DateTime;

import java.util.List;

@Data @Builder @JsonPathing(("$.d.results"))
public class SfCandidate {
    @JsonPathing({"$.lastName", "$.firstName"})
    private String name;         // 姓名
    @JsonPathing("$.candidateId")
    private String candidateId;  // 候选人ID
    @JsonPathing(value = "$.jobsApplied.results[0].gender", mapping = "Male,男,女")
    private String gender;       // 性别
    // /Date(631152000000+0000)/
    @JsonPathing(value = "$.jobsApplied.results[0].dateOfBirth", catchExpr = "\\d+")
    private DateTime birth;      // 出生日期
    @JsonPathing("$.cellPhone")
    private String cellPhone;    // 移动电话
    @JsonPathing("$.contactEmail")
    private String email;        // 电子邮件
    @JsonPathing(value = "$.education..custom6", filter = "@max",
            mapping = "649,高中及以下,650,高等职业学校,651,大学专科,652,大学本科,653,硕士研究生,654,博士研究生,未说明")
    private String edu;          // 最高学历
    @JsonPathing("$.education.results[?(@.custom6 == '#edu#')].major")
    private String major;        // 最高学历专业

    @JsonPathing(value = "$.outsideWorkExperience..startDate", catchExpr = "\\d+", filter = "@min")
    private DateTime workStartDate;  // 开始工作日期

    @Singular @JsonPathing("$.jobsApplied.results")
    private List<SfJobApply> jobApplies; // 申请职位

    @Data @Builder
    public static class SfJobApply {
        @JsonPathing("$.Dayeesouce")
        private String source;          // 来源， 内部/外部/猎头/自主招聘等等
        @JsonPathing("$.jobRequisition.positionNumber")
        private String positionNumber;  // 职位编号
        @JsonPathing("$.jobRequisition.jobReqLocale.results[0].jobTitle")
        private String jobTitle;        // 职位抬头
    }
}

