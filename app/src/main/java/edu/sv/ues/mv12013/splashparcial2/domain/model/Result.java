package edu.sv.ues.mv12013.splashparcial2.domain.model;

public class Result<T> {
    public final T data;
    public final String error;

    private Result(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) { return new Result<>(data, null); }
    public static <T> Result<T> error(String message) { return new Result<>(null, message); }

    public boolean isSuccess() { return error == null; }
}