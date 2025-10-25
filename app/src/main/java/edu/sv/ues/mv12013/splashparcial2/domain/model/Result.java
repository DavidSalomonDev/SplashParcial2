package edu.sv.ues.mv12013.splashparcial2.domain.model;

public class Result<T> {
    public final T data;
    public final String error;
    public final String message;

    private Result(T data, String error, String message) {
        this.data = data;
        this.error = error;
        this.message = message;
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(data, null, message);
    }
    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, null);
    }

    public boolean isSuccess() { return error == null; }
}