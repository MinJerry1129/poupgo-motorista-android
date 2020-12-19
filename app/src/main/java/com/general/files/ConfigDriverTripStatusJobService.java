package com.general.files;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class ConfigDriverTripStatusJobService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        try {
            if (MyApp.getInstance().getCurrentAct() != null) {
                ConfigDriverTripStatus.getInstance().executeTaskRun(() -> ConfigDriverTripStatusJobService.this.jobFinished(params, true));
            } else {
                ConfigDriverTripStatusJobService.this.jobFinished(params, true);
            }
        } catch (Exception e) {
            ConfigDriverTripStatusJobService.this.jobFinished(params, true);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
