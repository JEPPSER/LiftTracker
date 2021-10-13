import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { ActionSheetController, AlertController } from "@ionic/angular";
import { Exercise, ExerciseService } from "../../services/exersice.service";

@Component({
	selector: 'exercises-list',
	templateUrl: './exercises-list.component.html'
})
export class ExercisesListComponent {

	exercises: any[];

	constructor(
		private exService: ExerciseService,
		private alertController: AlertController,
		private actionSheetController: ActionSheetController,
		private router: Router,
		private route: ActivatedRoute
	) { }

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.exercises = this.exService.getExercises();
		});
	}

	async add() {
		const alert = await this.alertController.create({
			header: 'Lägg till',
			inputs: [
				{
					name: 'name',
					placeholder: 'Namn',
					type: 'text'
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
						this.exService.addExercise({ name: data.name });
						this.exercises = this.exService.getExercises();
					}
				}
			]
		});
		await alert.present();
	}

	async headerOptions() {
		const actionSheet = await this.actionSheetController.create({
			header: 'Alternativ',
			buttons: [
				{
					text: 'Lägg till övning',
					icon: 'add',
					handler: () => {
						this.add();
					}
				},
				{
					text: 'Lagring',
					icon: 'cloud-outline',
					handler: () => {
						this.router.navigateByUrl('storage');
					}
				}
			]
		});
		await actionSheet.present();
	}

	async options(event, ex: Exercise) {
		event.preventDefault();
		event.stopPropagation();
		const actionSheet = await this.actionSheetController.create({
			header: ex.name,
			cssClass: 'my-custom-class',
			buttons: [{
				text: 'Ta bort',
				role: 'destructive',
				icon: 'trash',
				handler: () => {
					this.confirmation(ex);
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

	async confirmation(ex: Exercise) {
		const alert = await this.alertController.create({
			header: 'Vill du ta bort övningen "' + ex.name + '"?',
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: () => {
						this.exService.deleteExercise(ex.exerciseId);
						this.exercises = this.exService.getExercises();
					}
				}
			]
		});
		await alert.present();
	}
}
