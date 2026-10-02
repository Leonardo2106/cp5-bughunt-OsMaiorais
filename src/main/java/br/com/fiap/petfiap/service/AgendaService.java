package br.com.fiap.petfiap.service;

import br.com.fiap.petfiap.exception.AtendimentoNaoEncontradoException;
import br.com.fiap.petfiap.exception.HorarioOcupadoException;
import br.com.fiap.petfiap.model.Atendimento;
import br.com.fiap.petfiap.repository.AtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// Regras de agenda do PetFiap: agendar, concluir e cancelar atendimentos.
@Service
public class AgendaService {

    @Autowired
    private AtendimentoRepository repository;

    // Agenda um novo atendimento: recusa data no passado e horario ja ocupado pelo mesmo pet.
    public Atendimento agendar(Atendimento novo) {
        if (novo.getDataHora() == null || novo.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e hora do atendimento nao podem estar no passado");
        }
        List<Atendimento> atendimentosDoPet = repository.findByPetNome(novo.getPetNome());
        for (Atendimento existente : atendimentosDoPet) {
            if (conflita(existente, novo)) {
                throw new HorarioOcupadoException(
                        "Pet " + novo.getPetNome() + " ja possui atendimento agendado nesse horario");
            }
        }
        return repository.save(novo);
    }

    // Conflito: mesmo pet, mesmo horario e atendimento existente ainda AGENDADO.
    private boolean conflita(Atendimento existente, Atendimento novo) {
        return existente.getPetNome().equals(novo.getPetNome())
                && existente.getDataHora().equals(novo.getDataHora())
                && Atendimento.AGENDADO.equals(existente.getStatus());
    }

    // Busca pelo id; nunca retorna null, o orElseThrow garante a excecao.
    public Atendimento buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AtendimentoNaoEncontradoException("Atendimento nao encontrado: " + id));
    }

    // Conclui o atendimento (status AGENDADO -> CONCLUIDO).
    public Atendimento concluir(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.concluir();
        return repository.save(atendimento);
    }

    // Cancela o atendimento (status AGENDADO -> CANCELADO).
    public Atendimento cancelar(Long id) {
        Atendimento atendimento = buscarPorId(id);
        atendimento.cancelar();
        return repository.save(atendimento);
    }

    // Lista os atendimentos de um pet.
    public List<Atendimento> buscarPorPet(String petNome) {
        return repository.findByPetNome(petNome);
    }
}
