package br.com.fiap.petfiap.model;

import br.com.fiap.petfiap.exception.StatusInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void deveDurar60MinutosQuandoAtendimentoForTosa() {
        // Arrange
        Atendimento tosa = new Tosa(24, "Rex", "PEQUENO", "Ana", data);

        // Act
        int duracao = tosa.getDuracaoMinutos();

        // Assert
        assertEquals(60, duracao);
    }

    @Test
    public void deveManterPrecoFixoQuandoConsultaTiverQualquerPorte() {
        // Arrange
        ConsultaVeterinaria pequena = new ConsultaVeterinaria(25, "Rex", "PEQUENO", "Ana", data);
        ConsultaVeterinaria media = new ConsultaVeterinaria(26, "Mimi", "MEDIO", "Bruno", data);
        ConsultaVeterinaria grande = new ConsultaVeterinaria(27, "Thor", "GRANDE", "Carla", data);

        // Act + Assert
        assertEquals(150.0, pequena.calcularPreco(), 0.001);
        assertEquals(150.0, media.calcularPreco(), 0.001);
        assertEquals(150.0, grande.calcularPreco(), 0.001);
    }

    @Test
    public void deveRecusarCancelamentoQuandoAtendimentoJaEstiverConcluido() {
        // Arrange: atendimento ja realizado
        Banho concluido = new Banho(28, "Rex", "PEQUENO", "Ana", data);
        concluido.concluir();

        // Act + Assert: nao se cancela o que ja foi realizado
        assertThrows(StatusInvalidoException.class, concluido::cancelar);
        assertEquals("CONCLUIDO", concluido.getStatus());
    }

    @Test
    public void deveRecusarConclusaoQuandoAtendimentoEstiverCancelado() {
        // Arrange
        Tosa cancelada = new Tosa(29, "Rex", "PEQUENO", "Ana", data);
        cancelada.cancelar();

        // Act + Assert: atendimento cancelado nao pode ser concluido
        assertThrows(StatusInvalidoException.class, cancelada::concluir);
        assertEquals("CANCELADO", cancelada.getStatus());
    }
}
