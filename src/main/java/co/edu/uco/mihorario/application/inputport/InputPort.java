package co.edu.uco.mihorario.application.inputport;

public interface InputPort<T, R> {
    R execute(T data);
}
