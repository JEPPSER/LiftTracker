import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { ExerciseDetailComponent } from './components/exercises/exercise-detail.component';
import { ExercisesListComponent } from './components/exercises/exercises-list.component';
import { WorkoutDetailComponent } from './components/workouts/workout-detail.component';

const routes: Routes = [
	{ path: '', component: ExercisesListComponent },
	{ path: ':exerciseId', component: ExerciseDetailComponent },
	{ path: ':exerciseId/:workoutId', component: WorkoutDetailComponent }
];

@NgModule({
	imports: [
		RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
	],
	exports: [RouterModule]
})
export class AppRoutingModule { }
