package ru.ilapin.hhminiclient;

import javax.inject.Singleton;

import dagger.Component;
import ru.ilapin.hhminiclient.backend.BackendModule;

@Singleton
@Component(modules = {SystemModule.class, BackendModule.class})
public interface ApplicationComponent {

	void inject(VacanciesListFragment fragment);
}
