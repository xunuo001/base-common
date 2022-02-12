package cn.huanzi.qch.baseadmin.wxpay.controller;

import lombok.Data;

@Data
public class AmountCallback {
    private long payerTotal;
    private long total;
}
