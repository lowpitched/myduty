package com.sinya.disruptor;

import com.lmax.disruptor.EventFactory;

public class SettleDetailFactory implements EventFactory<SettleDetail> {

    public SettleDetail newInstance() {
        return new SettleDetail();
    }
}
