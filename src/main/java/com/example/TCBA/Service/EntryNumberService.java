package com.example.TCBA.Service;

import com.example.TCBA.Entity.EntrySequence;
import com.example.TCBA.Repository.EntrySequenceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EntryNumberService {

    private final EntrySequenceRepository repository;

    @Transactional
    public String generate(String prefix) {

        EntrySequence seq = repository.findForUpdate(prefix);

        Long value = seq.getNextVal();
        seq.setNextVal(value + 1);
        repository.save(seq);

        int year = LocalDate.now().getYear();

        String padded;

        if (prefix.equals("DO")) {
            padded = String.format("%05d", value);
        } else {
            padded = String.format("%05d", value);
        }

        return prefix + "/" + year + "/" + padded;
    }
}
