package com.efei.proxy.event;

public abstract  class RetryTemplate {

    public abstract Object doService() throws Exception;

    private int count;

    private long delay;

    private long period;

    public int getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public Object execute(){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<count;i++){
            try{
                return doService();
            }catch (Exception e){
                try {
                    Thread.sleep(period);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
