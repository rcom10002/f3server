package info.knightrcom.model.plaything;

import info.knightrcom.model.plaything.MahjongWinningRule.FullRecordSupport;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 */
public class MahjongPointCalculator {

    /**
     * 规则容器
     */
    private static Map<String, Map<String, MahjongRule>> ruleContainer = new HashMap<String, Map<String, MahjongRule>>();
public static void main(String[] args) {
}
    static {
        // 装载默认规则
        try {
            // TODO 最好改成自动发现规则配置文件，暂时采用硬编码
            String[] propertiesFiles = {
                    "info/knightrcom/model/game/resources/mahjong.rule.properties",
                    "info/knightrcom/model/game/pushdownwin/pushdownwingame.common.rule.properties",
                    "info/knightrcom/model/game/pushdownwin/pushdownwingame.native.rule.properties"
            };
            for (String file : propertiesFiles) {
                Map<String, MahjongRule> mahjongRules = new LinkedHashMap<String, MahjongRule>();
                Properties props = new LinkedProperties();
                InputStream is = MahjongPointCalculator.class.getResourceAsStream(file);
                if (is == null) {
                    continue;
                }
                props.load(is);
                Enumeration<?> enumeration = props.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String ruleName = (String)enumeration.nextElement();
                    String ruleMeta = (String)props.getProperty(ruleName);
                    MahjongRule mahjongRule = new MahjongRule();
                    mahjongRule.setName(ruleName);
                    mahjongRule.setPoint(new Integer(ruleMeta.split(";")[0]));
                    if (ruleMeta.split(";").length == 2) {
                        mahjongRule.setConflicts(ruleMeta.split(";")[1]);
                    }
                    mahjongRules.put(ruleName, mahjongRule);
                }
                ruleContainer.put(file, mahjongRules);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param mahjongs 麻将记录
     * @param pointMark 标准番分
     * @param rulePropertiesPath 规则属性定义文件路径
     * @param ruleProcessorClazz 自定义规则判断类
     * @return
     */
    public static double calculatePointMark(String mahjongs, double pointMark, String rulePropertiesPath, Class<?> ruleProcessorClazz) {
        // 总得分
        double marks = 0;
        // 总番数
        int points = 1;
        try {
            // 规则装载
            Map<String, MahjongRule> myRules = ruleContainer.get(rulePropertiesPath);
            if (myRules == null) {
                myRules = new LinkedHashMap<String, MahjongRule>();
                Properties props = new LinkedProperties();
                props.load(MahjongPointCalculator.class.getResourceAsStream(rulePropertiesPath));
                Enumeration<?> keys = props.keys();
                while (keys.hasMoreElements()) {
                    String ruleName = (String)keys.nextElement();
                    String ruleMeta = (String)props.get(ruleName);
                    MahjongRule mahjongRule = new MahjongRule();
                    mahjongRule.setName(ruleName);
                    mahjongRule.setPoint(new Integer(ruleMeta.split(";")[0]));
                    if (ruleMeta.split(";").length == 2) {
                        mahjongRule.setCalculateMethod(ruleMeta.split(";")[1]);
                    } else {
                        mahjongRule.setConflicts(ruleMeta.split(";")[1]);
                        mahjongRule.setCalculateMethod(ruleMeta.split(";")[2]);
                    }
                    myRules.put(ruleName, mahjongRule);
                }
                ruleContainer.put(rulePropertiesPath, myRules);
            }

            // 番数计算
            List<MahjongRule> matchedRules = new ArrayList<MahjongRule>();
            Iterator<String> keyItr = myRules.keySet().iterator();
            while (keyItr.hasNext()) {
                String ruleName = keyItr.next();
                MahjongRule myRule = myRules.get(ruleName);
                boolean hasConflicts = false;
                for (MahjongRule eachMatchedRule : matchedRules) {
                    if (eachMatchedRule.isConflict(ruleName)) {
                        hasConflicts = true;
                        break;
                    }
                }
                if (hasConflicts) {
                    continue;
                }
                if (matchRule(ruleProcessorClazz, ruleName, mahjongs)) {
                    matchedRules.add(myRule);
                    if ("+".equals(myRule.getCalculateMethod())) {
                        marks += pointMark * myRule.getPoint();
                    } else if ("*".equals(myRule.getCalculateMethod())) {
                        points *= myRule.getPoint();
                        marks = pointMark * points;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return marks;
    }

    /**
     * @param ruleProcessorClazz
     * @param ruleName
     * @param mahjongs
     * @return
     */
    private static boolean matchRule(Class<?> ruleProcessorClazz, String ruleName, String mahjongs) {
        Object result = null;
        try {
            Method ruleMethod = ruleProcessorClazz.getMethod(ruleName, String.class);
            // 是否使用全记录。只需牌型分析的情况(清一色、碰碰和)，不使用全记录，而杠上开花这样需要分析之前记录的，需全记录
            if (!ruleMethod.isAnnotationPresent(FullRecordSupport.class)) {
                mahjongs = mahjongs.replaceAll(".*#(.*);", "$1");
            }
            result = ruleMethod.invoke(null, mahjongs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Boolean(result.toString());
    }

    /**
     * 对应
     * /info/knightrcom/model/plaything/mahjong.rule.properties
     * 或
     * /info/knightrcom/model/game/pushdownwin/pushdownwin.properties
     * 规则定义文件
     */
    private static class MahjongRule {

        /**
         * 规则名称
         */
        private String name;

        /**
         * 默认番数
         */
        private int point;

        /**
         * 规则冲突
         */
        private String conflicts;
        
        /**
         * 番数计算方法
         */
        private String calculateMethod;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the point
         */
        public int getPoint() {
            return point;
        }

        /**
         * @param point the point to set
         */
        public void setPoint(int point) {
            this.point = point;
        }

        /**
         * @param conflicts the conflicts to set
         */
        public void setConflicts(String conflicts) {
            this.conflicts = conflicts;
        }

        /**
         * @return the calculateMethod
         */
        public String getCalculateMethod() {
            return calculateMethod;
        }

        /**
         * @param calculateMethod the calculateMethod to set
         */
        public void setCalculateMethod(String calculateMethod) {
            this.calculateMethod = calculateMethod;
        }

        /**
         * @param ruleName
         * @return
         */
        public boolean isConflict(String ruleName) {
            return Arrays.binarySearch(conflicts.split(","), ruleName) > -1;
        }
    }

    /**
     * 保持顺序的Properties
     */
    private static class LinkedProperties extends Properties {

        private static final long serialVersionUID = 8984983672316158669L;

        private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

        public Enumeration<Object> keys() {
            return Collections.<Object>enumeration(keys);
        }

        public Object put(Object key, Object value) {
            keys.add(key);
            return super.put(key, value);
        }
    }
}
