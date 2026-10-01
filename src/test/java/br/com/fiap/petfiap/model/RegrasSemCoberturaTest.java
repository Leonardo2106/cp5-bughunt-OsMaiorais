package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Testes das regras do contrato que nao estavam cobertas pela suite recebida.
public class RegrasSemCoberturaTest {

    private final LocalDateTime data = LocalDateTime.of(2026, 12, 1, 10, 0);

    @Test
    public void deveCalcularPrecoPorPorteQuandoAtendimentoForBanho() {
        // Arrange
        Banho pequeno = new Banho(21, "Rex", "PEQUENO", "Ana", data);
        Banho medio = new Banho(22, "Mimi", "MEDIO", "Bruno", data);
        Banho grande = new Banho(23, "Thor", "GRANDE", "Carla", data);

        // Act + Assert
        assertEquals(60.0, pequeno.calcularPreco(), 0.001);
        assertEquals(80.0, medio.calcularPreco(), 0.001);
        assertEquals(100.0, grande.calcularPreco(), 0.001);
    }
}
