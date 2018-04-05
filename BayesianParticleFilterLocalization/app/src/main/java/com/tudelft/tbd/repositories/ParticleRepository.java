package com.tudelft.tbd.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.tudelft.tbd.dao.ParticleDescriptorDao;
import com.tudelft.tbd.databases.ParticleDatabase;
import com.tudelft.tbd.entities.ParticleDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Repository class that provides access to data in ParticleDatabase
 */
public class ParticleRepository {
    private ParticleDescriptorDao particleDescriptorDao;

    /**
     * Constructor
     * @param application: Application context
     */
    public ParticleRepository(Application application){
        ParticleDatabase db = ParticleDatabase.getDatabase(application);
        particleDescriptorDao = db.particleDescriptorDao();
    }

    public void insertAll(List<ParticleDescriptor> particles){
        (new InsertAll(particleDescriptorDao)).execute(particles.toArray(new ParticleDescriptor[]{}));
    }

    public void deleteAll(){
        (new DeleteAll(particleDescriptorDao)).execute();
    }

    public void deleteAllWithZeroWeights(){
        (new DeleteAllWithZeroWeights(particleDescriptorDao)).execute();
    }

    public void normalizeAllWeights(){
        (new NormalizeAllWeights(particleDescriptorDao)).execute();
    }

    public Float getSumOfAllWeights(){
        Float sum = null;
        try {
            sum = (new GetSumOfAllWeights(particleDescriptorDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public List<ParticleDescriptor> getAll(){
        List<ParticleDescriptor> particles = Collections.synchronizedList(new ArrayList<ParticleDescriptor>());
        try {
            particles = (new GetAll(particleDescriptorDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return particles;
    }

    public List<ParticleDescriptor> getAllFromFloor(int floor){
        List<ParticleDescriptor> particles = Collections.synchronizedList(new ArrayList<ParticleDescriptor>());
        try {
            particles = (new GetAllFromFloor(particleDescriptorDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return particles;
    }

    public List<ParticleDescriptor> getAllWithNonZeroWeights(){
        List<ParticleDescriptor> particles = Collections.synchronizedList(new ArrayList<ParticleDescriptor>());
        try {
            particles = (new GetAllWithNonZeroWeights(particleDescriptorDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return particles;
    }

    public List<ParticleDescriptor> getAllWithZeroWeights(){
        List<ParticleDescriptor> particles = Collections.synchronizedList(new ArrayList<ParticleDescriptor>());
        try {
            particles = (new GetAllWithZeroWeights(particleDescriptorDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return particles;
    }


    public ParticleDescriptor getClosestNonZeroParticle(int x, int y, int floor){
        ParticleDescriptor particle = null;
        try {
            particle = (new GetClosestNonZeroParticle(particleDescriptorDao)).execute(new Integer[]{x, y, floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return particle;
    }

    /**
     * AsyncTask wrappers around Dao methods
     */

    private static class InsertAll extends AsyncTask<ParticleDescriptor, Void, Void> {
        private ParticleDescriptorDao dao;

        InsertAll(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected Void doInBackground(ParticleDescriptor... particles) {
            dao.insertAll(particles);
            return null;
        }
    }


    private static class DeleteAll extends AsyncTask<Void, Void, Void> {
        private ParticleDescriptorDao dao;

        DeleteAll(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAll();
            return null;
        }
    }


    private static class NormalizeAllWeights extends AsyncTask<Void, Void, Void> {
        private ParticleDescriptorDao dao;

        NormalizeAllWeights(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            float sum = dao.getSumOfAllWeights();
            dao.normalizeAllWeights(sum);
            return null;
        }
    }


    private static class DeleteAllWithZeroWeights extends AsyncTask<Void, Void, Void> {
        private ParticleDescriptorDao dao;

        DeleteAllWithZeroWeights(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllWithZeroWeights();
            return null;
        }
    }

    private static class GetSumOfAllWeights extends AsyncTask<Void, Void, Float> {
        private ParticleDescriptorDao dao;

        GetSumOfAllWeights(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected Float doInBackground(Void... voids) {
            return dao.getSumOfAllWeights();
        }
    }

    private static class GetAll extends AsyncTask<Void, Void, List<ParticleDescriptor>> {
        private ParticleDescriptorDao dao;

        GetAll(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected List<ParticleDescriptor> doInBackground(Void... voids) {
            return dao.getAll();
        }
    }

    private static class GetAllFromFloor extends AsyncTask<Integer, Void, List<ParticleDescriptor>> {
        private ParticleDescriptorDao dao;

        GetAllFromFloor(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected List<ParticleDescriptor> doInBackground(Integer... floors) {
            return dao.getAllFromFloor(floors[0]);
        }
    }

    private static class GetAllWithNonZeroWeights extends AsyncTask<Void, Void, List<ParticleDescriptor>> {
        private ParticleDescriptorDao dao;

        GetAllWithNonZeroWeights(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected List<ParticleDescriptor> doInBackground(Void... voids) {
            return dao.getAllWithNonZeroWeights();
        }
    }

    private static class GetAllWithZeroWeights extends AsyncTask<Void, Void, List<ParticleDescriptor>> {
        private ParticleDescriptorDao dao;

        GetAllWithZeroWeights(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected List<ParticleDescriptor> doInBackground(Void... voids) {
            return dao.getAllWithZeroWeights();
        }
    }

    private static class GetClosestNonZeroParticle extends AsyncTask<Integer, Void, ParticleDescriptor> {
        private ParticleDescriptorDao dao;

        GetClosestNonZeroParticle(ParticleDescriptorDao particleDescriptorDao){
            this.dao = particleDescriptorDao;
        }

        @Override
        protected ParticleDescriptor doInBackground(Integer... params) {
            return dao.getClosestNonZeroParticle(params[0], params[1], params[2]);
        }
    }
}
