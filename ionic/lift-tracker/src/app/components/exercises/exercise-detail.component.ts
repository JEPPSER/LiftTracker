import { Component, ViewChild } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertController } from "@ionic/angular";
import { FormService } from "src/app/services/form.service";
import { Set } from "src/app/services/set.service";
import { Workout, WorkoutService } from "src/app/services/workout.service";
import { Exercise, ExerciseService } from "../../services/exersice.service";
import { PlotPoint, ScatterPlotComponent } from "../graphs/scatter-plot.component";

@Component({
	selector: 'exercise-detail',
	templateUrl: 'exercise-detail.component.html'
})
export class ExerciseDetailComponent {

	exercise: Exercise;
	exerciseId: string;

	data: PlotPoint[];

	state: string = 'workouts';
	drawLine: boolean = true;
	propDates: boolean = false;
	statProp: string = 'volume';
	show: string = '20';
	view: string = 'days';

	@ViewChild('Plot') plot: ScatterPlotComponent;

	constructor(
		private exService: ExerciseService,
		private route: ActivatedRoute,
		private alertController: AlertController,
		private workoutService: WorkoutService,
		private formService: FormService,
		private router: Router
	) {}

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.exerciseId = params['exerciseId'];
			if (this.exerciseId) {
				this.getExercise();
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
							date.setHours(0);
							date.setMinutes(0);
							date.setMilliseconds(0);
							this.router.navigateByUrl(this.router.url + '/create_' + date.toISOString());
							//let workout: Workout = { exerciseId: this.exercise.exerciseId, date: date };
							//this.workoutService.addWorkout({ exerciseId: this.exercise.exerciseId, date: date });
							//this.getExercise();
						}
					}
				}
			]
		});
		await alert.present();
	}

	async deleteWorkout(workout) {
		//this.workoutService.deleteWorkout(workout);
		for (let set of workout.sets) {
			await this.deleteSet(set.formContentGuid);
		}
		this.getExercise();
	}

	deleteSet(guid): Promise<void> {
		return new Promise<void>(resolve => {
			this.formService.deleteFormContent(guid).subscribe(res => {
				resolve();
			}, err => {
				resolve();
			});
		});
	}

	getExercise() {
		let formGuid = localStorage.getItem('pasteKey');
		if (formGuid) {
			this.formService.getFormContent(formGuid).subscribe(res => {
				let workouts = [];
				for (let set of res) {
					let dateStr = set.values.find(v => v.formFieldId == 408687)?.value;
					if (dateStr) {
						let temp = new Date(dateStr);
						let date = new Date(temp.getFullYear(), temp.getMonth(), temp.getDate());
						let workoutId = date.toISOString();
						let workout = workouts.find(w => w.workoutId == workoutId);

						let wSet: Set = {
							weight: set.values.find(v => v.formFieldId == 408689)?.value,
							reps: set.values.find(v => v.formFieldId == 408690)?.value,
							workoutId: workoutId,
							formContentGuid: set.formContentGuid
						};

						if (!workout) {
							workouts.push({ date: date, workoutId: workoutId, sets: [ wSet ] });
						} else {
							workout.sets.push(wSet);
						}
					}
				}
				this.exercise = { workouts: workouts, name: decodeURIComponent(this.exerciseId) };
				this.updatePlot();
			});
		}
		//this.exercise = this.exService.getExercise(this.exerciseId);
	}

	onViewChanged(event) {
		this.view = event.detail.value;
		this.propDates = false;
		this.plot.propDates = this.propDates;
		this.plot.view = this.view;
		this.plot.draw()
	}

	onShowChanged(event) {
		this.show = event.detail.value;
		this.updatePlot();
	}

	updatePlot() {
		this.data = [];
		while (this.data.length > 0) {
			this.data.splice(0, 1);
		}

		this.view = 'days';

		let endIndex = this.exercise.workouts.length;

		if (this.show != 'all') {
			let showCount = parseInt(this.show);
			if (this.exercise.workouts.length > showCount) {
				endIndex = showCount;
			}
		}

		for (let i = 0; i < endIndex; i++) {
			let w = this.exercise.workouts[i];
			if (this.statProp == 'volume') {
				let volume = 0;
				for (let s of w.sets) {
					volume += (s.weight * s.reps);
				}
				let p: PlotPoint = { date: w.date, value: volume, week: 0 };
				this.data.push(p);
			} else if (this.statProp == 'maxWeight') {
				let max = 0;
				for (let s of w.sets) {
					if (+s.weight > +max) {
						max = s.weight;
					}
				}
				let p: PlotPoint = { date: w.date, value: max, week: 0 };
				this.data.push(p);
			} else if (this.statProp == 'maxReps') {
				let max = 0;
				for (let s of w.sets) {
					if (+s.reps > +max) {
						max = s.reps;
					}
				}
				let p: PlotPoint = { date: w.date, value: max, week: 0 };
				this.data.push(p);
			} else if (this.statProp == 'totalSets') {
				let p: PlotPoint = { date: w.date, value: w.sets.length, week: 0 };
				this.data.push(p);
			} else if (this.statProp == 'totalReps') {
				let val: number = 0;
				for (let s of w.sets) {
					val = +val + +s.reps;
				}
				let p: PlotPoint = { date: w.date, value: val, week: 0 };
				this.data.push(p);
			}
		}

		if (this.plot) {
			this.plot.data = this.data;
			this.plot.view = this.view;
			this.plot.draw();
		}
	}
}
