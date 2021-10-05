import { Component, ViewChild } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AlertController } from "@ionic/angular";
import { WorkoutService } from "src/app/services/workout.service";
import { Exercise, ExerciseService } from "../../services/exersice.service";
import { PlotPoint, ScatterPlotComponent } from "../graphs/scatter-plot.component";

@Component({
	selector: 'exercise-detail',
	templateUrl: 'exercise-detail.component.html'
})
export class ExerciseDetailComponent {

	exercise: Exercise;
	exerciseId: number;

	data: PlotPoint[];

	state: string = 'workouts';
	drawLine: boolean = true;
	propDates: boolean = false;
	statProp: string = 'volume';
	view: string = 'days';

	@ViewChild('Plot') plot: ScatterPlotComponent;

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
							this.workoutService.addWorkout({ exerciseId: this.exercise.exerciseId, date: date });
							this.getExercise();
						}
					}
				}
			]
		});
		await alert.present();
	}

	deleteWorkout(workout) {
		this.workoutService.deleteWorkout(workout);
		this.getExercise();
	}

	getExercise() {
		this.exercise = this.exService.getExercise(this.exerciseId);
		this.updatePlot();
	}

	onViewChanged(event) {
		this.view = event.detail.value;
		this.propDates = false;
		this.plot.propDates = this.propDates;
		this.plot.view = this.view;
		this.plot.draw()
	}

	updatePlot() {
		this.data = [];
		while (this.data.length > 0) {
			this.data.splice(0, 1);
		}

		this.view = 'days';

		for (let w of this.exercise.workouts) {
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
