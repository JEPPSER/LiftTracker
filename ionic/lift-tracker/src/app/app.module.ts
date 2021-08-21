import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ExercisesListComponent } from './exercises/exercises-list.component';
import { IonicStorageModule, Storage } from '@ionic/storage-angular';
import { ExerciseService } from './services/exersice.service';
import { WorkoutService } from './services/workout.service';
import { SetService } from './services/set.service';

@NgModule({
	declarations: [
		AppComponent,
		ExercisesListComponent
	],
	entryComponents: [],
	imports: [
		BrowserModule,
		IonicModule.forRoot(),
		AppRoutingModule
	],
	providers: [
		{ provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
		ExerciseService,
		WorkoutService,
		SetService
	],
	bootstrap: [AppComponent],
})
export class AppModule { }
