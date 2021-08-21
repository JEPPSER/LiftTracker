import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ExercisesListComponent } from './components/exercises/exercises-list.component';
import { ExerciseService } from './services/exersice.service';
import { WorkoutService } from './services/workout.service';
import { SetService } from './services/set.service';
import { ExerciseDetailComponent } from './components/exercises/exercise-detail.component';
import { WorkoutsListComponent } from './components/workouts/workouts-list.component';
import { CommonModule } from '@angular/common';
import { WorkoutDetailComponent } from './components/workouts/workout-detail.component';
import { ScatterPlotComponent } from './components/graphs/scatter-plot.component';

@NgModule({
	declarations: [
		AppComponent,
		ExercisesListComponent,
		ExerciseDetailComponent,
		WorkoutsListComponent,
		WorkoutDetailComponent,
		ScatterPlotComponent
	],
	entryComponents: [],
	imports: [
		BrowserModule,
		IonicModule.forRoot(),
		AppRoutingModule,
		CommonModule
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
