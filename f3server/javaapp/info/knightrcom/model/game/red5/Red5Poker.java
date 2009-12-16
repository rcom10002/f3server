package info.knightrcom.model.game.red5;

import info.knightrcom.model.plaything.PokerColor;
import info.knightrcom.model.plaything.PokerValue;
import info.knightrcom.util.ModelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 红五扑克
 */
public class Red5Poker {

    public static final String PRIORITY_SEQUENCE = "V3,V4,V6,V7,V8,V9,V10,VJ,VQ,VK,VA,V2,V5,VX,VY";
    private PokerColor colorStyle;
    private PokerValue valueStyle;
    private int priorityValue;

    public Red5Poker(PokerColor colorStyle, PokerValue valueStyle) {
        this.colorStyle = colorStyle;
        this.valueStyle = valueStyle;
        this.priorityValue = PRIORITY_SEQUENCE.indexOf(valueStyle.name()) * 10;
        if (PokerValue.V5.equals(valueStyle) && PokerColor.HEART.equals(colorStyle)) {
            this.priorityValue += 3;
        }
    }

    /**
     * @return the priorityValue
     */
    public int getPriorityValue() {
        return this.priorityValue;
    }

    /**
     * @return the colorStyle
     */
    public PokerColor getColorStyle() {
        return colorStyle;
    }

    /**
     * @return the valueStyle
     */
    public PokerValue getValueStyle() {
        return valueStyle;
    }

    /**
     * @return
     */
    public String getValue() {
        return colorStyle.ordinal() + valueStyle.name();
    }

    /**
     * 自适应洗牌
     * 
     * @param forPuppet
     * @return
     */
    public static Red5Poker[][] shuffle(boolean forPuppet) {
        if (!forPuppet) {
            return shuffle();
        }
        // 准备自定义用的扑克
        List<String> customPokersList = null;
        customPokersList = ModelUtil.getSystemParameters("CUSTOM_POKER_RED5");
        String [] allPokers = customPokersList.get(new Random().nextInt(customPokersList.size())).split("~");
        int allPokersLength = allPokers[0].split(",").length + allPokers[1].split(",").length;
        // 计算每个玩家手中牌数
        int eachPokerLength = 
            (allPokersLength % Red5Game.PLAYER_COGAME_NUMBER) == 0 ? 
                (allPokersLength / Red5Game.PLAYER_COGAME_NUMBER) : 
                    (allPokersLength / Red5Game.PLAYER_COGAME_NUMBER) + 1;
        // 开始发牌
        Red5Poker[][] eachShuffledPokers = new Red5Poker[Red5Game.PLAYER_COGAME_NUMBER][eachPokerLength];
        String[] allUncustomPokers = allPokers[1].split(",");
        // 部分洗牌操作
        List<String> pokersForShuffling = Arrays.asList(allUncustomPokers);
        Collections.shuffle(pokersForShuffling);
        allUncustomPokers = pokersForShuffling.toArray(new String[]{});
        int currentSide = 0;
        for (int i = 0; i < allUncustomPokers.length; i += (Red5Game.PLAYER_COGAME_NUMBER - 1)) {
            for (int j = 0; j < eachShuffledPokers.length - 1; j++) {
                if (i + j == allUncustomPokers.length) {
                    break;
                }
                String currentPoker = allUncustomPokers[i + j];
                PokerColor currentColor = PokerColor.values()[new Integer(String.valueOf(currentPoker.charAt(0)))];
                PokerValue currentValue = PokerValue.valueOf(currentPoker.substring(1));
                eachShuffledPokers[j][currentSide] = new Red5Poker(currentColor, currentValue);
            }
            currentSide++;
        }
        String[] allCustomPokers = allPokers[0].split(",");
        for (int i = 0; i < allCustomPokers.length; i++) {
            PokerColor currentColor = PokerColor.values()[new Integer(String.valueOf(allCustomPokers[i].charAt(0)))];
            PokerValue currentValue = PokerValue.valueOf(allCustomPokers[i].substring(1));
            eachShuffledPokers[Red5Game.PLAYER_COGAME_NUMBER - 1][i] = new Red5Poker(currentColor, currentValue);
        }
        return eachShuffledPokers;
    }

    /**
     * 常规洗牌
     * 
     * @return
     */
    private static Red5Poker[][] shuffle() {
        // 准备洗牌用的扑克
        List<Red5Poker> pokers = new ArrayList<Red5Poker>();
        // 10至大王的索引为7到15
        List<PokerValue> list = Arrays.asList(PokerValue.values()).subList(7, 15);
        // 准备两副扑克
        for (int i = 0; i < 2; i++) {
            for (PokerValue pokerValue : list) {
                if (PokerValue.VX.equals(pokerValue) || PokerValue.VY.equals(pokerValue)) {
                    pokers.add(new Red5Poker(PokerColor.NO_COLOR, pokerValue));
                    continue;
                }
                Red5Poker a = new Red5Poker(PokerColor.HEART, pokerValue);
                Red5Poker b = new Red5Poker(PokerColor.DIAMOND, pokerValue);
                Red5Poker c = new Red5Poker(PokerColor.CLUB, pokerValue);
                Red5Poker d = new Red5Poker(PokerColor.SPADE, pokerValue);
                pokers.add(a);
                pokers.add(b);
                pokers.add(c);
                pokers.add(d);
            }
            Red5Poker a = new Red5Poker(PokerColor.HEART, PokerValue.V5);
            Red5Poker b = new Red5Poker(PokerColor.DIAMOND, PokerValue.V5);
            Red5Poker c = new Red5Poker(PokerColor.CLUB, PokerValue.V5);
            Red5Poker d = new Red5Poker(PokerColor.SPADE, PokerValue.V5);
            pokers.add(a);
            pokers.add(b);
            pokers.add(c);
            pokers.add(d);
        }
        // 开始随机洗牌
        Collections.shuffle(pokers);
        // 计算每个玩家手中牌数
        int eachPokerLength = 
            (pokers.size() % Red5Game.PLAYER_COGAME_NUMBER) == 0 ? 
                (pokers.size() / Red5Game.PLAYER_COGAME_NUMBER) : 
                    (pokers.size() / Red5Game.PLAYER_COGAME_NUMBER) + 1;
        // 开始发牌
        Red5Poker[][] eachShuffledPokers = new Red5Poker[Red5Game.PLAYER_COGAME_NUMBER][eachPokerLength];
        int currentSide = 0;
        for (int i = 0; i < pokers.size(); i += Red5Game.PLAYER_COGAME_NUMBER) {
            for (int j = 0; j < eachShuffledPokers.length; j++) {
                if (i + j == pokers.size()) {
                    break;
                }
                eachShuffledPokers[j][currentSide] = pokers.get(i + j);
            }
            currentSide++;
        }
        return eachShuffledPokers;
    }

    /**
     * 按GM需求洗牌
     * 
     * @return
     */
    public static Red5Poker[][] shuffle(String cheatCards) {
        return null;
    }
}
