<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="file://${path}/css/iconfont.css" type="text/css"/>
    <link rel="stylesheet" href="file://${path}/css/cm.css" type="text/css"/>
</head>
<body>
<div class="main-container" id="main-container">
    <div class="main bg-white clearfix">
        <h2 class="top-head ta-c">星宇集团人员面试过程信息</h2>
        <#if applicant??>
            <div class="hd-s1">
                <div class="i"></div>
                <span>应聘岗位信息</span>
            </div>
            <table class="tb-list-info ta-l">
                <colgroup>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                </colgroup>
                <tbody>
                <tr>
                    <td class="label-in-tb">应聘公司</td>
                    <td colspan="3">${applicant.head.apporg!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">应聘职位</td>
                    <td>${applicant.head.appjob}</td>
                    <td class="label-in-tb">人员类型</td>
                    <td>${applicant.head.psncl!''}</td>
                </tr>
                <#if applicant.head.fronttype??&&applicant.head.fronttype=='一线岗'>
                    <tr>
                        <td class="label-in-tb">一线岗类别</td>
                        <td>${applicant.head.fronttype!''}</td>
                        <td class="label-in-tb">所属劳务公司</td>
                        <td>${applicant.head.belongcorp!''}</td>
                    </tr>
                </#if>
                <tr>
                    <td class="label-in-tb">招聘渠道</td>
                    <td>${applicant.head.channel!''}</td>
                    <td class="label-in-tb">期望税前月薪</td>
                    <td>${applicant.head.vmoney!''}</td>
                </tr>
                </tbody>
            </table>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>个人基本信息</span>
            </div>
            <table class="tb-list-info ta-l">
                <colgroup>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                </colgroup>
                <tbody>
                <tr>
                    <td class="label-in-tb">姓名</td>
                    <td>${applicant.head.vname!''}</td>
                    <td class="label-in-tb">身份证</td>
                    <td>${applicant.head.psnid!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">民族</td>
                    <td>${applicant.head.nation!''}</td>
                    <td class="label-in-tb">性别</td>
                    <td>${applicant.head.vsex!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">籍贯</td>
                    <td>${applicant.head.vnative!''}</td>
                    <td class="label-in-tb">是否应届生</td>
                    <td>
                        <#if applicant.head.ifstudent??&&applicant.head.ifstudent!=''>${(applicant.head.ifstudent=='Y')?string('是','否')}</#if>
                    </td>
                </tr>
                <tr>
                    <td class="label-in-tb">家庭住址</td>
                    <td colspan="3">${applicant.head.familyaddress!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">政治面貌</td>
                    <td>${applicant.head.political!''}</td>
                    <td class="label-in-tb">学历</td>
                    <td>${applicant.head.education!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">学习方式</td>
                    <td>${applicant.head.studymode!''}</td>
                    <td class="label-in-tb">血型</td>
                    <td>${applicant.head.bloodtype!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">身高(CM)</td>
                    <td>${applicant.head.height!''}</td>
                    <td class="label-in-tb">联系方式</td>
                    <td>${applicant.head.phone!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">参加工作时间</td>
                    <td>${applicant.head.joinworkdate!''}</td>
                    <td class="label-in-tb">婚姻状况</td>
                    <td>${applicant.head.marriage!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">外语</td>
                    <td colspan="3">${applicant.head.foreign!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">外语等级</td>
                    <td colspan="3">${applicant.head.foreignlevel!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">所持证书</td>
                    <td colspan="3">${applicant.head.certificate!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">现居住地</td>
                    <td colspan="3">${applicant.head.address!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">邮箱</td>
                    <td colspan="3">${applicant.head.email!''}</td>
                </tr>
                </tbody>
            </table>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>是否有直系亲属/朋友与本公司存在业务往来</span>
            </div>
            <#if applicant.head.ifbusiness != 'Y'>
                <span>否</span>
            <#else>
                <table class="tb-list-info ta-l">
                    <colgroup>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                    </colgroup>
                    <tbody>

                    <tr>
                        <td class="label-in-tb">姓名</td>
                        <td>${applicant.head.bname!''}</td>
                        <td class="label-in-tb">与本人关系</td>
                        <td>${applicant.head.brelation!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">单位名称</td>
                        <td colspan="3">${applicant.head.bcorp!''}</td>
                    </tr>
                    </tbody>
                </table>
            </#if>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>是否有推荐人</span>
            </div>
            <#if applicant.head.ifrecommender != 'Y'>
                <span>否</span>
            <#else>
                <table class="tb-list-info ta-l">
                    <colgroup>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                    </colgroup>
                    <tbody>

                    <tr>
                        <td class="label-in-tb">推荐人</td>
                        <td>${applicant.head.mname!''}</td>
                        <td class="label-in-tb">与本人关系</td>
                        <td>${applicant.head.mrelaition!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">备注</td>
                        <td colspan="3">${applicant.head.mmemo!''}</td>
                    </tr>
                    </tbody>
                </table>
            </#if>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>是否有亲属/朋友在本公司</span>
            </div>
            <#if applicant.head.ifrelative != 'Y'>
                <span>否</span>
            <#else>
                <#if applicant.relation??>
                    <#list applicant.relation as relation>
                        <div style="padding: 10px 0">内部关系（${relation_index+1}）</div>
                        <table class="tb-list-info ta-l">
                            <colgroup>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                            </colgroup>
                            <tbody>

                            <tr>
                                <td class="label-in-tb">亲属姓名</td>
                                <td>${relation.name!''}</td>
                                <td class="label-in-tb">与本人关系</td>
                                <td>${relation.relation!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">备注</td>
                                <td colspan="3">${relation.memo!''}</td>
                            </tr>
                            </tbody>
                        </table>
                    </#list>
                </#if>
            </#if>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>是否有工作经历</span>
            </div>
            <#if applicant.head.ifwork != 'Y'>
                <span>否</span>
            <#else>
                <#if applicant.work??>
                    <#list applicant.work as work>
                        <div style="padding: 10px 0">工作经历（${work_index+1}）</div>
                        <table class="tb-list-info ta-l">
                            <colgroup>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                            </colgroup>
                            <tbody>
                            <tr>
                                <td class="label-in-tb">单位名称</td>
                                <td colspan="3">${work.workcorp!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">起始日期</td>
                                <td>${work.begindate!''}</td>
                                <td class="label-in-tb">终止日期</td>
                                <td>${work.enddate!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">薪资</td>
                                <td>${work.workachive!''}</td>
                                <td class="label-in-tb">岗位</td>
                                <td>${work.workduty!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">证明人</td>
                                <td>${work.certifier!''}</td>
                                <td class="label-in-tb">证明人电话</td>
                                <td>${work.certiphone!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">离职原因</td>
                                <td colspan="3">${work.dismismatter!''}</td>
                            </tr>
                            </tbody>
                        </table>
                    </#list>
                </#if>
            </#if>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>教育经历</span>
            </div>
            <#list applicant.edu as edu>
                <div style="padding: 10px 0">教育经历（${edu_index+1}）</div>
                <table class="tb-list-info ta-l">
                    <colgroup>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                    </colgroup>
                    <tbody>
                    <tr>
                        <td class="label-in-tb">学校名称</td>
                        <td colspan="3">${edu.school!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">起始日期</td>
                        <td>${edu.begindate!''}</td>
                        <td class="label-in-tb">终止日期</td>
                        <td>${edu.enddate!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">学历</td>
                        <td>${edu.education!''}</td>
                        <td class="label-in-tb">是否最高学历</td>
                        <td>${(edu.lastflag=='Y')?string('是','否')}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">学习方式</td>
                        <td>${edu.studymode!''}</td>
                        <td class="label-in-tb">专业</td>
                        <td>${edu.major!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">学院</td>
                        <td colspan="3">${edu.college!''}</td>
                    </tr>
                    </tbody>
                </table>
            </#list>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>家庭状况</span>
            </div>
            <#list applicant.family as family>
                <div style="padding: 10px 0">家庭状况（${family_index+1}）</div>
                <table class="tb-list-info ta-l">
                    <colgroup>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                        <col style="width:18%;"/>
                        <col style="width:32%;"/>
                    </colgroup>
                    <tbody>
                    <tr>
                        <td class="label-in-tb">称谓</td>
                        <td>${family.mem_relation!''}</td>
                        <td class="label-in-tb">姓名</td>
                        <td>${family.mem_name!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">年龄</td>
                        <td>${family.age!''}</td>
                        <td class="label-in-tb">联系电话</td>
                        <td>${family.phone!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">工作单位</td>
                        <td colspan="3">${family.mem_corp!''}</td>
                    </tr>
                    <tr>
                        <td class="label-in-tb">岗位</td>
                        <td colspan="3">${family.mem_job!''}</td>
                    </tr>
                    </tbody>
                </table>
            </#list>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>承诺书</span>
            </div>
            <div class="note">
                1、本人无任何纹身，无赌博、吸毒等不良嗜好，无犯罪史，并保证所填写的资料真实有效；否则视为隐瞒真相，本人愿接受公司任何处罚并作自动离职处理。<br/>
                2、本人无任何重大疾病史（含脑部手术、体内任何器官的缺损、骨折等）、无慢性病史（含心脏病、高血压、癫痫病、精神病、脑部其他疾病等）。
            </div>
            <div class="signpad mt10">
                <img style="width:200px;height: 200px;"
                     src="data:image/png;base64,${applicant.head.person!''}">
            </div>
            <div class="hd-s1 mt20">
                <div class="i"></div>
                <span>面试过程</span>
            </div>
            <#if applicant.interviewer?? && (applicant.interviewer?size>0)>
                <#assign interIndex=1 />
                <#list applicant.interviewer as interviewer>
                    <#if interviewer.ifpass??>
                        <div style="padding: 10px 0">面试过程（${interIndex}）</div>
                        <table class="tb-list-info ta-l">
                            <colgroup>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                                <col style="width:18%;"/>
                                <col style="width:32%;"/>
                            </colgroup>
                            <tbody>
                            <tr>
                                <td class="label-in-tb">面试官</td>
                                <td>${interviewer.user_name!''}</td>
                                <td class="label-in-tb">审批日期</td>
                                <td>${interviewer.approve_date!''}</td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">面试类型</td>
                                <td>
                                    <#if interviewer.type=='MS01'>一线岗面试
                                    <#elseif interviewer.type=='MS03'>管理岗初试
                                    <#elseif interviewer.type=='MS05'>HRBP确认</#if>
                                </td>
                                <td class="label-in-tb">是否通过</td>
                                <td>
                                    <#if interviewer.ifpass??>
                                        ${(interviewer.ifpass=='Y')?string('是','否')}
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td class="label-in-tb">审批意见</td>
                                <td colspan="3">${interviewer.memo!''}</td>
                            </tr>
                            </tbody>
                        </table>
                        <#assign interIndex=interIndex+1/>
                    </#if>
                </#list>
            <#else>
                <span>无数据</span>
            </#if>
        <#else>
            ${errorMessage}
        </#if>
    </div>
</div>
</body>
</html>
