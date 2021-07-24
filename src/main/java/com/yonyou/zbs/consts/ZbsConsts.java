package com.yonyou.zbs.consts;

public interface ZbsConsts {
    String STEEL_TYPE_1 = "1";
    String STEEL_TYPE_2 = "2";
    String STEEL_TYPE_3 = "3";
    String STEEL_TYPE_4 = "4";

    String ZBS_TYPE_M = "M";
    String ZBS_TYPE_S = "S";
    String[] M_HEAD_COLUMNS = new String[]{
            "ID",
            "cNO",
            "cCertificateNo",// 质保书编号
            "cSign",
            "cCustomer",// 客户名称
            "dDate",// 日期
            "cStellGrade",// 钢号
            "cStellGradeW",
            "cContractNO",// 和同号
            "cProductName",
            "cCERTIFICATE",
            "cPROCESS",
            "cDEFORMATION",
            "cGSIZE",
            "cDELIVERY",
            "cULTRASONIC",
            "cDECARBURIZATION",
            "cQUALITY",
            "iSteelType",
            "cOperator",
            "QRCode",
            "bupload",
            "cNOTES1",
            "cNOTES2",
            "cSPECIFICATION",// 规格
            "cStellDesc",// 描述
            "cGroupName", // 中文工厂名称
            "enGroupName", // 英文工厂名称
            "enAddress"// 英文厂址

    };

    String[] M_BODY_COLUMNS = new String[]{
            "i_id",
            "cNO",
            "millId",
            "cCertificateNo",
            "cHEATNO",// 炉批号
            "cSIZES",// 规格
            "Condition",// 条件
            "cPCS",// 件数
            "dWeight",// 重量
            "cFields1",
            "cFields2",
            "cFields3",
            "cFields4",
            "cFields5",
            "cFields6",
            "cFields7",
            "cFields8",
            "cFields9",
            "cFields10",
            "cFields11",
            "cFields12",
            "cFields13",
            "cFields14",
            "cFields15",
            "cFields16",
            "cFields17",
            "cFields18",
            "cANNEALLING",
            "A_T",
            "A_H",
            "B_T",
            "B_H",
            "C_T",
            "C_H",
            "D_T",
            "D_H",
            "cHardness",
            "cPorosity",
            "cSegregation",
            "cDistribution",
            "cSize",
            "cGrainSize",
            "cMICROSTRUCTURE",
            "cMICROHOMOGENITY",
            "iSteelType",
            "itype"
    };
    String[] M_ELEMENTS = new String[]{
            "C",
            "Si",
            "Mn",
            "P",
            "S",
            "W",
            "Mo",
            "Cr",
            "V",
            "Cu",
            "Ni",
            "Co",
            "Al",
            "Pb",
            "Sn",
            "Ti",
            "B",
            "Nb"
    };

    String[] M_NONMETALLIC = new String[]{
            "A_H",
            "A_T",
            "B_H",
            "B_T",
            "C_H",
            "C_T",
            "D_H",
            "D_T"
    };

    interface M1 {
        String ABBR = "TGMY";
        String[] HEADER_FIELDS = new String[]{
                "cCertificateNo",// 质保书编号
                "cCustomer",// 客户名称
                "dDate",// 日期
                "cStellGrade",// 钢号
                "cContractNO",// 和同号
                "cSPECIFICATION",// 规格
                "cStellDesc",// 描述
                "cGroupName", // 中文工厂名称
                "enGroupName", // 英文工厂名称
                "enAddress",// 英文厂址
                "cNOTES1",
                "cNOTES2"
        };
        String[] BODY_FIELDS = new String[]{
                "cHEATNO",// 炉批号
                "cSIZES",// 规格
                "Condition",// 条件
                "cPCS",// 件数
                "dWeight",// 重量
                "cHardness",// 硬度
                "cGrainSize",// 颗粒度
                "A_H",
                "A_T",
                "B_H",
                "B_T",
                "C_H",
                "C_T",
                "D_H",
                "D_T",
                "cFields1",
                "cFields2",
                "cFields3",
                "cFields4",
                "cFields5",
                "cFields6",
                "cFields7",
                "cFields8",
                "cFields9",
                "cFields10",
                "cFields11",
                "cFields12",
                "cFields13",
                "cFields14",
                "cFields15"
        };
        String[] REF_FIELDS = new String[]{
                "C",
                "Si",
                "Mn",
                "P",
                "S",
                "Cr",
                "Mo",
                "V",
                "W",
                "Co",
                "Cu",
                "Ni",
                "H2",
                "O2",
                "N2",
                "A_T",
                "A_H",
                "B_T",
                "B_H",
                "C_T",
                "C_H",
                "D_T",
                "D_H",
                "cHardness",
                "cGrainSize"
        };
        String[] ELEMENTS_FIELDS = new String[]{
                "C",
                "Si",
                "Mn",
                "P",
                "S",
                "Cr",
                "Mo",
                "V",
                "W",
                "Co",
                "Cu",
                "Ni",
                "H2",
                "O2",
                "N2"
        };
    }
}
