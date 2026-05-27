package co.edu.uco.mihorario.application.usecase;

public interface UseCase<D, R> {
    R execute(D data);
}
