import { Injectable } from "@angular/core";
import { Workout } from "./workout.service";

@Injectable()
export class ExerciseService {

	private readonly EX_STORAGE: string = 'exercises';

	constructor(
	) {}

	getExercises(): Exercise[] {
		let list = JSON.parse(localStorage.getItem(this.EX_STORAGE));
		if (!list) {
			list = [];
		}
		return list;
	}

	getExercise(exerciseId: number): Exercise {
		let list = this.getExercises();
		let ex = list.find(e => e.exerciseId = exerciseId);
		return ex;
	}

	addExercise(exercise: Exercise) {
		let list = this.getExercises();
		exercise.workouts = [];
		exercise.exerciseId = this.getNewId();
		list.push(exercise);
		localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
	}

	deleteExercise(exerciseId: number) {
		let list = this.getExercises();
		let index = list.findIndex(e => e.exerciseId = exerciseId);
		list.splice(index, 1);
		localStorage.setItem(this.EX_STORAGE, JSON.stringify(list));
	}

	getNewId() {
		let list = this.getExercises();
		let max = 0;
		for (let l of list) {
			if (l.exerciseId > max) {
				max = l.exerciseId;
			}
		}
		return max + 1;
	}
}

export interface Exercise {
	exerciseId?: number;
	name: string;
	workouts?: Workout[];
}
