package com.example.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eze.executor.AppExecutors;
import com.example.eze.model.Professor;
import com.example.eze.room.database.EzeDatabase;
import com.example.eze.room.dao.ProfessorDao;

import java.util.List;

public class ProfessorRepository {
    private final ProfessorDao professorDao;
    private LiveData<List<Professor>> professorLiveDate;

    public ProfessorRepository(Application application) {
        EzeDatabase ezeDatabase = EzeDatabase.getInstance(application);
        professorDao = ezeDatabase.professorDao();
        professorLiveDate = professorDao.getAllProfessor();
    }

    public void insert(Professor professor){
        AppExecutors.getInstance().getDiskIO().execute(new InsertRunnable(professorDao, professor));
    }

    public void delete(Professor professor){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteRunnable(professorDao, professor));
    }

    public void update(Professor professor){
        AppExecutors.getInstance().getDiskIO().execute(new UpdateRunnable(professorDao, professor));
    }

    public void deleteAll(){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteAllRunnable(professorDao));
    }

    public LiveData<List<Professor>> getAllProfessor(){
        return professorLiveDate;
    }

    private static class InsertRunnable implements Runnable{
        private final ProfessorDao professorDao;
        private final Professor professor;

        public InsertRunnable(ProfessorDao professorDao, Professor professor) {
            this.professorDao = professorDao;
            this.professor = professor;
        }

        @Override
        public void run() {
            professorDao.insertProfessor(professor);
        }
    }

    private static class UpdateRunnable implements Runnable{
        private final ProfessorDao professorDao;
        private final Professor professor;

        public UpdateRunnable(ProfessorDao professorDao, Professor professor) {
            this.professorDao = professorDao;
            this.professor = professor;
        }

        @Override
        public void run() {
            professorDao.updateProfessor(professor);
        }
    }

    private static class DeleteRunnable implements Runnable{
        private final ProfessorDao professorDao;
        private final Professor professor;

        public DeleteRunnable(ProfessorDao professorDao, Professor professor) {
            this.professorDao = professorDao;
            this.professor = professor;
        }

        @Override
        public void run() {
            professorDao.deleteProfessor(professor);
        }
    }

    private static class DeleteAllRunnable implements Runnable{
        private final ProfessorDao professorDao;

        public DeleteAllRunnable(ProfessorDao professorDao) {
            this.professorDao = professorDao;
        }

        @Override
        public void run() {
            professorDao.deleteAllProfessor();
        }
    }
}
