package net.fxft.ascsareavoice.ltmonitor.vo;

import java.util.List;

/**
 * 分页结果，主要是为了迎合前端 easyui分页表格的要求
 *
 * @author admin
 */
public class PaginateResult {

    public static final int CODE_SUCCESS = 10000;

    private boolean success;
    private int code;
    private String message;

    private long total;

    private List rows;

    public long getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public PaginateResult(long _total, List _rows) {
        this.success = true;
        this.code = CODE_SUCCESS;
        total = _total;
        rows = _rows;
    }

    public PaginateResult() {
        this.success = true;
        this.code = CODE_SUCCESS;
    }

    public PaginateResult(int code, String message) {
        this.success = false;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
