package io.github.gabrielmsouza.catalogo.application;

public abstract class UnitUseCase<IN> {

    public abstract void execute(IN anIn);
}