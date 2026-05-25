package co.edu.uco.miHorario.application.usecase;

public interface UseCase<D, R> {
    R execute(D data);
}
