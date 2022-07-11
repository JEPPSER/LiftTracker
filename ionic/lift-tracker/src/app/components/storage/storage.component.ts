import { Component } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AlertController, LoadingController, ToastController } from "@ionic/angular";
import { PasteService } from "src/app/services/paste.service";

@Component({
	selector: 'storage',
	templateUrl: 'storage.component.html',
	styles: [`
		pre {

		}
	`]
})
export class StorageComponent {

	state: string = 'data';

	data: any;
	backup: any;
	backupRaw: string;

	isLoading: boolean = false;
	error: boolean = false;

	constructor(
		private pasteService: PasteService,
		private loadingController: LoadingController,
		private alertController: AlertController,
		private toastController: ToastController,
		private route: ActivatedRoute,
		public router: Router
	) { }

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.error = false;
			//this.getData();
		});
	}

	getData() {
		this.isLoading = true;
		this.data = JSON.parse(localStorage.getItem('exercises'));
		this.pasteService.getData('backup').then(backup => {
			if (!backup) {
				this.presentToast('Fel vid hämtning av backup data', 'danger');
				this.error = true;
			} else {
				this.backupRaw = backup;
				this.backup = JSON.parse(backup);
			}
			this.isLoading = false;
		});
	}

	async presentToast(text: string, color: string) {
		const toast = await this.toastController.create({
			message: text,
			color: color,
			duration: 2000
		});
		await toast.present();
	}

	async downloadAlert() {
		const alert = await this.alertController.create({
			header: 'Hämta backup',
			message: 'Är det säkert att du vill ersätta din lokala data med backup datan?',
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: () => {
						this.downloadBackup();
					}
				}
			]
		});
		await alert.present();
	}

	downloadBackup() {
		localStorage.setItem('exercises', this.backupRaw);
		this.presentLoading(1000, 'Hämtar...');
		this.data = JSON.parse(localStorage.getItem('exercises'));
	}

	async updateAlert() {
		const alert = await this.alertController.create({
			header: 'Uppdatera backup',
			message: 'Är det säkert att du vill uppdatera din backup?',
			buttons: [
				{
					text: 'Avbryt',
					role: 'cancel',
					handler: () => {

					}
				}, {
					text: 'OK',
					handler: () => {
						this.updateBackup();
					}
				}
			]
		});
		await alert.present();
	}

	updateBackup() {
		this.presentLoading();
		this.pasteService.postData('backup', localStorage.getItem('exercises')).then(() => {
			this.loadingController.dismiss();
			this.getData();
		}, error => {
			this.loadingController.dismiss();
		});
	}

	async presentLoading(duration: number = null, text: string = 'Laddar upp...') {
		const loading = await this.loadingController.create({
			message: text,
			duration: duration
		});
		await loading.present();
	}
}
