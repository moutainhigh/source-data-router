package com.globalegrow.bts;
import java.util.Map;

public class RecommendTypeUtil {
    public static final String RECOMMEND_MAP_KEY = "recommend_type";

    public enum RecommendTypes {
        reindex("2010102"), regoodsdetail("2020201"), recart("2030101"), _skip("_skip");

        private String value;


        RecommendTypes(String value) { this.value = value; }



        public String getValue() { return this.value; }
    }











    public static void recommendType(Map<String, Object> outJson) { outJson.put("recommend_type", recommendTye(outJson)); }



    public static String recommendTye(Map<String, Object> outJson) {
        String glb_plf = String.valueOf(outJson.get("glb_plf"));
        String glb_b = String.valueOf(outJson.get("glb_b"));
        String mrlc = String.valueOf(outJson.get("mrlc"));
        String fmd = String.valueOf(outJson.get("fmd"));

        if ("c".equals(glb_b) && "pc".equals(glb_plf) && "T_3".equals(mrlc))
            return RecommendTypes.regoodsdetail.getValue();
        if ("pc".equals(glb_plf) && "mr_T_3".equals(fmd))
            return RecommendTypes.regoodsdetail.getValue();
        if (("pc".equals(glb_plf) || "m".equals(glb_plf)) && "c".equals(glb_b)) {
            return RecommendTypes.regoodsdetail.getValue();
        }

        if ("pc".equals(glb_plf) && "d".equals(glb_b) && "T_8".equals(mrlc))
            return RecommendTypes.recart.getValue();
        if ("pc".equals(glb_plf) && "mr_T_8".equals(fmd))
            return RecommendTypes.recart.getValue();
        if ("pc".equals(glb_plf) && "d".equals(glb_b)) {
            return RecommendTypes.recart.getValue();
        }

        if ("a".equals(glb_b) && "pc".equals(glb_plf) && "T_1".equals(mrlc))
            return RecommendTypes.reindex.getValue();
        if ("pc".equals(glb_plf) && "mr_T_1".equals(fmd))
            return RecommendTypes.reindex.getValue();
        if (("pc".equals(glb_plf) || "m".equals(glb_plf)) && "a".equals(glb_b)) {
            return RecommendTypes.reindex.getValue();
        }
        return RecommendTypes._skip.getValue();
    }


    public static String getRecommendTypeByFmd(String fmd) {
        switch (fmd) {
            case "mr_T_1":
                return RecommendTypes.reindex.getValue();
            case "mr_T_3":
                return RecommendTypes.regoodsdetail.getValue();
            case "mr_T_8":
                return RecommendTypes.recart.getValue();
        }
        return null;
    }


    public static String getRecommendTypeByMrlc(String mrlc) {
        switch (mrlc) {
            case "T_1":
                return RecommendTypes.reindex.getValue();
            case "T_3":
                return RecommendTypes.regoodsdetail.getValue();
            case "T_8":
                return RecommendTypes.recart.getValue();
        }
        return null;
    }


    public static String getRecommendTypeByPlfAndB(String plf, String b) {
        if (("pc".equals(plf) || "m".equals(plf)) && "c".equals(b)) {
            return RecommendTypes.regoodsdetail.getValue();
        }
        if ("pc".equals(plf) && "d".equals(b)) {
            return RecommendTypes.recart.getValue();
        }
        if (("pc".equals(plf) || "m".equals(plf)) && "a".equals(b)) {
            return RecommendTypes.reindex.getValue();
        }
        return null;
    }
}
