import { Component, EventEmitter, Input, Output } from "@angular/core";
import { ActionSheetController, AlertController } from "@ionic/angular";
import { Workout } from "src/app/services/workout.service";

@Component({
	selector: 'workouts-list',
	templateUrl: 'workouts-list.component.html'
})
export class WorkoutsListComponent {

	@Input() workouts;
	@Output() onDeleteWorkout: EventEmitter<any> = new EventEmitter<any>();

	constructor(
		private actionSheetController: ActionSheetController,
		private alertController: AlertController
	) {}

	ngOnInit() {

	}

	getHeaviestSet(workout: Workout): string {
		let set = workout.sets[0];
		for (let s of workout.sets) {
			if (+s.weight > +set.weight) {
				set = s;
			}
		}
		return set ? set.weight + ' kg x ' + set.reps : '';
	}

	async options(event, workout: Workout) {
		event.preventDefault();
		event.stopPropagation();
		const actionSheet = await this.actionSheetController.create({
			cssClass: 'my-custom-class',
			buttons: [{
				text: 'Ta bort',
				role: 'destructive',
				icon: 'trash',
				handler: () => {
					this.confirmation(workout);
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

	async confirmation(workout: Workout) {
		const alert = await this.alertController.create({
			header: 'Vill du ta bort detta passet?',
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: () => {
						this.onDeleteWorkout.emit(workout);
					}
				}
			]
		});
		await alert.present();
	}

	sortWorkouts() {
		let items = this.workouts.sort((a: any, b: any) =>
        	new Date(b.date).getTime() - new Date(a.date).getTime()
    	);
		return items;
	}
}
