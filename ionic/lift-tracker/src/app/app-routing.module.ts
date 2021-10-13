import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { ExerciseDetailComponent } from './components/exercises/exercise-detail.component';
import { ExercisesListComponent } from './components/exercises/exercises-list.component';
import { SecurityComponent } from './components/storage/security.component';
import { StorageComponent } from './components/storage/storage.component';
import { WorkoutDetailComponent } from './components/workouts/workout-detail.component';

const routes: Routes = [
	{ path: '', component: ExercisesListComponent },
	{ path: 'storage', component: StorageComponent },
	{ path: 'storage/security', component: SecurityComponent },
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
