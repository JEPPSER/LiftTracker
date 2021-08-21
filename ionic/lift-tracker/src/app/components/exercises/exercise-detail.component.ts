import { Component } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AlertController } from "@ionic/angular";
import { WorkoutService } from "src/app/services/workout.service";
import { Exercise, ExerciseService } from "../../services/exersice.service";

@Component({
	selector: 'exercise-detail',
	templateUrl: 'exercise-detail.component.html'
})
export class ExerciseDetailComponent {

	exercise: Exercise;
	exerciseId: number;

	state: string = 'workouts';

	constructor(
		private exService: ExerciseService,
		private route: ActivatedRoute,
		private alertController: AlertController,
		private workoutService: WorkoutService
	) {}

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.exerciseId = params['exerciseId'];
			if (this.exerciseId) {
				this.exercise = this.exService.getExercise(this.exerciseId);
			}
		});
	}

	async add() {
		const alert = await this.alertController.create({
			header: 'Lägg till',
			inputs: [
				{
					name: 'date',
					type: 'date'
				}
			],
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: (data) => {
						if (data.date != '') {
							let date = new Date(data.date);
							this.workoutService.addWorkout({ exerciseId: this.exercise.exerciseId, date: date });
							this.exercise = this.exService.getExercise(this.exerciseId);
						}
					}
				}
			]
		});
		await alert.present();
	}

	deleteWorkout(workout) {
		this.workoutService.deleteWorkout(workout);
		this.exercise = this.exService.getExercise(this.exerciseId);
	}
}
