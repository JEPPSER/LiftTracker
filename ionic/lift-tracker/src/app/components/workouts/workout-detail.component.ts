import { Component } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ActionSheetController, AlertController } from "@ionic/angular";
import { SetService } from "src/app/services/set.service";
import { Workout, WorkoutService } from "src/app/services/workout.service";
import { Set } from "src/app/services/set.service";

@Component({
	selector: 'workout-detail',
	templateUrl: 'workout-detail.component.html'
})
export class WorkoutDetailComponent {

	workout: Workout;
	workoutId: number;

	constructor(
		private route: ActivatedRoute,
		private workoutService: WorkoutService,
		private alertController: AlertController,
		private setService: SetService,
		private actionSheetController: ActionSheetController
	) {}

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.workoutId = params['workoutId'];
			if (this.workoutId) {
				this.workout = this.workoutService.getWorkout(this.workoutId);
			}
		});
	}

	async add() {
		const alert = await this.alertController.create({
			header: 'Lägg till',
			inputs: [
				{
					name: 'weight',
					placeholder: 'Vikt',
					type: 'number'
				}, {
					name: 'reps',
					placeholder: 'Reps',
					type: 'number'
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
						this.setService.addSet({ workoutId: this.workoutId, reps: data.reps, weight: data.weight });
						this.workout = this.workoutService.getWorkout(this.workoutId);
					}
				}
			]
		});
		await alert.present();
	}

	async options(event, set: Set) {
		event.preventDefault();
		event.stopPropagation();
		const actionSheet = await this.actionSheetController.create({
			cssClass: 'my-custom-class',
			buttons: [{
				text: 'Kopiera',
				icon: 'copy',
				handler: () => {
					this.setService.copySet(set);
					this.workout = this.workoutService.getWorkout(this.workoutId);
				}
			},{
				text: 'Ta bort',
				role: 'destructive',
				icon: 'trash',
				handler: () => {
					this.confirmation(set);
				}
			}, {
				text: 'Avbryt',
				icon: 'close',
				role: 'cancel',
				handler: () => {
				}
			}]
		});
		await actionSheet.present();
	}

	async confirmation(set: Set) {
		const alert = await this.alertController.create({
			header: 'Vill du ta bort detta settet?',
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: () => {
						this.setService.deleteSet(set);
						this.workout = this.workoutService.getWorkout(this.workoutId);
					}
				}
			]
		});
		await alert.present();
	}
}
