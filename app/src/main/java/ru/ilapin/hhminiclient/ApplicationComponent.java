package ru.ilapin.hhminiclient;

import javax.inject.Singleton;

import dagger.Component;
import ru.ilapin.hhminiclient.vacanciesrepository.VacanciesRepository;

@Singleton
@Component(modules = {SystemModule.class})
public interface ApplicationComponent {

	void inject(VacanciesRepository repository);
}
