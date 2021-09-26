package com.example.eze.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eze.model.Professor;
import com.example.eze.room.repository.ProfessorRepository;

import java.util.List;

public class ProfessorViewModel extends AndroidViewModel {
    private ProfessorRepository professorRepository;
    private LiveData<List<Professor>> allProfessor;

    public ProfessorViewModel(@NonNull Application application) {
        super(application);
        professorRepository = new ProfessorRepository(application);
        allProfessor = professorRepository.getAllProfessor();
    }

    public void insert(Professor professor){
        professorRepository.insert(professor);
    }

    public void update(Professor professor){
        professorRepository.update(professor);
    }

    public void delete(Professor professor){
        professorRepository.delete(professor);
    }

    public void deleteAll(){
        professorRepository.deleteAll();
    }

    public LiveData<List<Professor>> getAllProfessor(){
        return allProfessor;
    }
}
