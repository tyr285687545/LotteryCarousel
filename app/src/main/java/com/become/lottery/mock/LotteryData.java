package com.become.lottery.mock;


import com.become.lottery.vo.PrizeEntity;

import java.util.ArrayList;

/**
 * Created by TangYaoRong On 2020/7/2
 */
public class LotteryData {
    private static LotteryData instance;
    private PrizeEntity prizeEntity;
    private ArrayList<PrizeEntity> prizeArray = new ArrayList<>();

    public static LotteryData getInstance() {
        if (instance == null) {
            instance = new LotteryData();
        }
        return instance;
    }

    private LotteryData() {

    }

    public ArrayList<PrizeEntity> getData() {
        prizeArray.clear();
        prizeEntity = new PrizeEntity();
        prizeEntity.setName("加湿器");
        prizeEntity.setChecked(true);
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.name = "华硕游戏本";
        prizeEntity.setChecked(false);
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.name = "飞科剃须刀";
        prizeEntity.setChecked(false);
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.setChecked(false);
        prizeEntity.name = "扫地机器人";
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.setChecked(false);
        prizeEntity.name = "iPhone11";
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.setChecked(false);
        prizeEntity.name = "山地自行车";
        prizeArray.add(prizeEntity);

        prizeEntity = new PrizeEntity();
        prizeEntity.name = "30元话费";
        prizeEntity.setChecked(false);
        prizeArray.add(prizeEntity);

//        prizeEntity = new PrizeEntity();
//        prizeEntity.name = "床上四件套";
//        prizeEntity.setChecked(false);
//        prizeArray.add(prizeEntity);
        return prizeArray;
    }
}
