package info.knightrcom.command;

public abstract class HandlerSecurity {
    public HandlerSecurity() {
        if (this.getClass().getFields().length > 3) {
            throw new RuntimeException("句柄中存在自定义共享变量！");
        }
    }
}
