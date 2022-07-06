import { Injectable } from "@angular/core";
import { ExerciseService } from "./exersice.service";
import { Set } from './set.service';

@Injectable()
export class WorkoutService {

	private readonly EX_STORAGE: string = 'exercises';

	constructor(
		private exService: ExerciseService
	) {}

	getWorkout(workoutId: string): Workout {
		let list = this.exService.getExercises();
		let workout;
		for (let ex of list) {
			workout = ex.workouts.find(w => w.workoutId == workoutId);
			if (workout) {
				break;
			}
		}
		return workout;
	}

	addWorkout(workout: Workout) {
		let list = this.exService.getExercises();
		let ex = list.find(e => e.exerciseId == workout.exerciseId);
		workout.sets = [];
		//workout.workoutId = this.getNewId();
		ex.workouts.push(workout);
		localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
	}

	deleteWorkout(workout: Workout) {
		let list = this.exService.getExercises();
		let ex = list.find(e => e.exerciseId == workout.exerciseId);
		let index = ex.workouts.findIndex(w => w.workoutId == workout.workoutId);
		ex.workouts.splice(index, 1);
		localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
	}

	getNewId() {
		let list = this.exService.getExercises();
		let max = 0;
		for (let l of list) {
			for (let w of l.workouts) {
				/*if (w.workoutId > max) {
					max = w.workoutId;
				}*/
			}
		}
		return max + 1;
	}
}

export interface Workout {
	workoutId?: string;
	exerciseId: string;
	date: Date;
	sets?: Set[];
}
