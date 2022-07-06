import { Injectable } from "@angular/core";
import { ExerciseService } from "./exersice.service";

@Injectable()
export class SetService {

	private readonly EX_STORAGE: string = 'exercises';

	constructor(
		private exService: ExerciseService
	) {}

	getSets() {

	}

	deleteSet(set: Set) {
		let list = this.exService.getExercises();
		let workout;
		for (let ex of list) {
			workout = ex.workouts.find(w => w.workoutId == set.workoutId);
			if (workout) {
				break;
			}
		}
		let index = workout.sets.findIndex(s => s.setId == set.setId);
		workout.sets.splice(index, 1);
		localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
	}

	addSet(set: Set) {
		let list = this.exService.getExercises();
		let workout;
		for (let ex of list) {
			workout = ex.workouts.find(w => w.workoutId == set.workoutId);
			if (workout) {
				break;
			}
		}
		if (workout) {
			set.setId = this.getNewId();
			workout.sets.push(set);
			localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
		}
	}

	copySet(set: Set) {
		let list = this.exService.getExercises();
		let workout;
		for (let ex of list) {
			workout = ex.workouts.find(w => w.workoutId == set.workoutId);
			if (workout) {
				break;
			}
		}
		if (workout) {
			let newSet = { reps: set.reps, weight: set.weight, workoutId: set.workoutId, setId: this.getNewId() };
			workout.sets.push(newSet);
			localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
		}
	}

	getNewId() {
		let list = this.exService.getExercises();
		let max = 0;
		for (let l of list) {
			for (let w of l.workouts) {
				for (let s of w.sets) {
					if (s.setId > max) {
						max = s.setId;
					}
				}
			}
		}
		return max + 1;
	}
}

export interface Set {
	setId?: number;
	workoutId: string;
	reps: number;
	weight: number;
}
