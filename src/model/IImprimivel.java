package model;

public interface IImprimivel {

    String getDetalhesFormatados();

    default void imprimirDetalhes() {
        System.out.println(getDetalhesFormatados());
    }
}