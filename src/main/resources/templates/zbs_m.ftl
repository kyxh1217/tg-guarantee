<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="file://${path}/css/iconfont.css" type="text/css"/>
    <link rel="stylesheet" href="file://${path}/css/cm.css" type="text/css"/>
    <style>
        .line-l1 {
            padding: 0 10px 0 10px;
            float: left
        }

        .batch-container {
            border: 1px solid #999999;
            padding: 6px 6px 0 6px;
            margin-top: 8px;
            font-size: 10px;
        }
    </style>
</head>
<body>
<div class="main-container" id="main-container" style="page-break-after: always;">
    <div class="main bg-white clearfix">
        <h2 class="top-head ta-c">MILL TEST CERTIFICATE</h2>
        <#if head??>
            <div style="display: flex;flex-direction: row;; flex-wrap: nowrap">
                <div style="flex: 1;margin-right:10px">
                    <img alt="" src="file://${path}/img/logo.jpg" style="height: 50px;width: 50px"/>
                </div>
                <div style="font-size: 12px;text-align: left;flex: auto;width: 100%">
                    JIANGSU TIANGONG TOOLS COMPANY<p>HouXiang Town,DanYang City, JiangSu Province,China www.tggj.cn
                    <p>Certificate No.:${head.cCertificateNo!''}
                </div>
            </div>
            <table class="tb-list-info ta-l" style="font-size: 10px;margin-top: 8px;font-weight: normal">
                <colgroup>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                    <col style="width:18%;"/>
                    <col style="width:32%;"/>
                </colgroup>
                <tbody>
                <tr>
                    <td class="label-in-tb">CUSTOMER:</td>
                    <td colspan="3">${head.cCustomer!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">COMMODITY:</td>
                    <td colspan="3">HIGH SPEED STEEL ROUND BAR</td>
                </tr>
                <tr>
                    <td class="label-in-tb">ISSUE DATE:</td>
                    <td>${head.dDate!''}</td>
                    <td class="label-in-tb">GRADE:</td>
                    <td>${head.cStellGradeW!''}</td>
                </tr>
                <tr>
                    <td class="label-in-tb">SPECIFICATION:</td>
                    <td>${head.cSPECIFICATION!''}</td>
                    <td class="label-in-tb">CONTRACT NO.:</td>
                    <td>${head.cContractNO!''}</td>
                </tr>
                </tbody>
            </table>
        </#if>
        <div class="hd-s1 mt10">
            <div class="i"></div>
            <span>REFERENCE RANGE</span>
        </div>
        <#if ref??>
            <div class="batch-container clearfix">
                <#list ["C","Si","Mn","P","S","W","Mo","Cr","V","Cu","Ni","Co","Al","Pb","Sn","Ti","B","Nb"] as elem>
                    <#if ref[elem]?? && ref[elem]!=''>
                        <div style="width: 10%;float: left;padding: 3px 0;">
                            <div style="float: left">${elem}:</div>
                            <div style="padding: 0 4px;float: left">${ref[elem]}</div>
                        </div>
                    </#if>
                </#list>
            </div>
        </#if>
        <div class="hd-s1 mt10">
            <div class="i"></div>
            <span>BATCH LIST</span>
        </div>
        <#if batchList??>
            <#list batchList as batch>
                <#assign bSize=batchList?size>
                <div style="<#if (batch_index%5==2)||(bSize>4&&batch_index==(bSize-1)&&(batch_index-3)%5==3)||(bSize==2&&batch_index==1)>page-break-after: always;</#if>"
                     class="batch-container clearfix">
                    <div style="width: 100%;" class="clearfix">
                        <div style="float: left;width: 33%">
                            <div style="float: left">cHEATNO:</div>
                            <div class="line-l1">${batch.cHEATNO!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left">SIZES(mm):</div>
                            <div class="line-l1">${batch.cSIZES!''}</div>
                        </div>
                        <#if batch.Condition?? && batch.Condition!=''>
                            <div style="float: left;width: 33%">
                                <div style="float: left">Condition:</div>
                                <div class="line-l1">${batch.Condition!''}</div>
                            </div>
                        </#if>
                        <#if batch.cGrainSize?? && batch.cGrainSize!=''>
                            <div style="float: left;width: 33%">
                                <div style="float: left">Grain size:</div>
                                <div class="line-l1">${batch.cGrainSize!''}</div>
                            </div>
                        </#if>
                        <div style="float: left;width: 33%">
                            <div style="float: left"> NO. OF PCS:</div>
                            <div class="line-l1">${batch.cPCS!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left"> Net Weight(kg):</div>
                            <div class="line-l1">${batch.dWeight!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left">ANNEALLING HB:</div>
                            <div class="line-l1">${batch.cANNEALLING!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left">Quenched-Tempered Hardness:</div>
                            <div class="line-l1">${batch.cHardness!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left">Carbides Distribution As Per " SEP:</div>
                            <div class="line-l1">${batch.cDistribution!''}</div>
                        </div>
                        <div style="float: left;width: 33%">
                            <div style="float: left">Carbide Size:</div>
                            <div class="line-l1">${batch.cSize!''}</div>
                        </div>
                    </div>
                    <div style="width: 100%;height: 100%; border-top:1px dotted #999999;margin: 3px 0;padding: 3px 0"
                         class="clearfix">
                        <div style="float: left;margin-right: 10px;padding: 3px 0">CHEM. ANALYSIS (%)</div>
                        <#list ["C","Si","Mn","P","S","W","Mo","Cr","V","Cu","Ni","Co","Al","Pb","Sn","Ti","B","Nb"] as elem>
                            <#if batch["cFields"+(elem_index+1)]?? && batch["cFields"+(elem_index+1)]!=''>
                                <div style="width: 10%;float: left;padding: 3px 0;">
                                    <div style="float: left">${elem}:</div>
                                    <div style="padding: 0 4px;float: left">${batch["cFields"+(elem_index+1)]}</div>
                                </div>
                            </#if>
                        </#list>
                        <#list ["C","Si","Mn","P","S","W","Mo","Cr","V","Cu","Ni","Co","Al","Pb","Sn","Ti","B","Nb"] as elem>
                            <#if batch["cFields"+(elem_index+1)]?? && batch["cFields"+(elem_index+1)]!=''>
                                <div style="width: 10%;float: left;padding: 3px 0;">
                                    <div style="float: left">${elem}:</div>
                                    <div style="padding: 0 4px;float: left">${batch["cFields"+(elem_index+1)]}</div>
                                </div>
                            </#if>
                        </#list>
                    </div>
                    <div style="width: 100%;height: 100%; border-top:1px dotted #999999;margin: 3px 0;padding: 3px 0"
                         class="clearfix">
                        <div style="float: left;margin-right: 10px;padding: 3px 0">Nonmetallic Inclusisons</div>
                        <#list ["A","B","C","D"] as l1>
                            <div style="width: 10%;float: left;padding: 3px 0;">
                                <div style="float: left">${l1}-T:</div>
                                <div style="padding: 0 4px;float: left">${batch[l1+"_T"]}</div>
                            </div>
                            <div style="width: 10%;float: left;padding: 3px 0;">
                                <div style="float: left">${l1}-H:</div>
                                <div style="padding: 0 4px;float: left">${batch[l1+"_H"]}</div>
                            </div>
                        </#list>
                    </div>
                    <div style="width: 100%;height: 100%; border-top:1px dotted #999999;margin: 3px 0;padding: 3px 0"
                         class="clearfix">
                        <div style="float: left;margin-right: 10px;padding: 3px 0">Macroscopic Structure</div>
                        <div style="width: 10%;float: left;padding: 3px 0;">
                            <div style="float: left">cPorosit:</div>
                            <div style="padding: 0 4px;float: left">${batch.cPorosity}</div>
                        </div>
                        <div style="width: 10%;float: left;padding: 3px 0;">
                            <div style="float: left">cSegreg:</div>
                            <div style="padding: 0 4px;float: left">${batch.cSegregation}</div>
                        </div>
                    </div>
                </div>
            </#list>
        </#if>
        <div class="hd-s1 mt10">
            <div class="i"></div>
            <span>NOTES</span>
        </div>
        <div style="border:1px solid #999999;padding: 8px ;margin-top:10px;font-size: 10px;"
             class="clearfix">
            <div style="width: 100%;height: 100%;padding: 3px;min-height: 40px" class="clearfix">
                ${head.cNOTES1!''}
            </div>
            <div style="width: 100%;height: 100%;padding: 3px; border-top:1px dotted #999999;margin: 3px 0;min-height: 40px"
                 class="clearfix">
                ${head.cNOTES2!''}
            </div>
        </div>
        <div class="clearfix" style="font-size: 10px;margin-top:10px;">
            <div style="padding: 0 4px;float: left;width: 33%">Department: Quality Control Dept.</div>
            <div style="padding: 0 4px;float: left;width: 33%">Chief Of Inspector: ZhuZhenmei</div>
        </div>
        <div class="clearfix" style="font-size: 10px;margin-top:10px;">
            <div style="float: right;width:160px;text-align: center">
                <img alt="" src="file://${path}/img/tg.png" style="height: 100px;width:124px"/>
            </div>
            <div style="float: right;width: 160px;text-align: center">
                <img alt="" src="data:image/png;base64,${qrBase64}" style="height: 100px;width:100px"/>
            </div>
        </div>
    </div>
</div>
</body>
</html>
