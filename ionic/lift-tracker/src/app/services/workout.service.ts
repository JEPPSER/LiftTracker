import { Injectable } from "@angular/core";

@Injectable()
export class WorkoutService {

	private readonly EX_STORAGE: string = 'exercises';

	constructor(
		private storage: Storage
	) {}

	getWorkouts() {

	}

	addWorkout() {

	}
}

export interface Workout {
	workoutId?: number;
	date: Date;
	order: number;
}
