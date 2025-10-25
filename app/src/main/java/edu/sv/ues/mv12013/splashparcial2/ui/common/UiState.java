package edu.sv.ues.mv12013.splashparcial2.ui.common;

public abstract class UiState {
    public static final UiState Idle = new IdleState();
    public static final UiState Loading = new LoadingState();
    public static final UiState Success = new SuccessState();

    public static final class IdleState extends UiState { private IdleState() {} }
    public static final class LoadingState extends UiState { private LoadingState() {} }
    public static final class SuccessState extends UiState { private SuccessState() {} }
    public static final class Error extends UiState {
        public final String message;
        public Error(String message) { this.message = message; }
    }
}