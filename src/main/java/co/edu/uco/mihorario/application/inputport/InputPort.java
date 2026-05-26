package co.edu.uco.miHorario.application.inputport;

public interface InputPort<T, R> {
    R execute(T data);
}
