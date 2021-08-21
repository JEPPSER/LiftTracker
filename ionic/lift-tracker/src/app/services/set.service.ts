import { Injectable } from "@angular/core";

@Injectable()
export class SetService {

	private readonly EX_STORAGE: string = 'exercises';

	constructor(
		private storage: Storage
	) {}

	getSets() {

	}

	addSet() {

	}
}

export interface Set {
	setId?: number;
	reps: number;
	weight: number;
}
