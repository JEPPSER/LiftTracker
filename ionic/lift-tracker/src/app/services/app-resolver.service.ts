import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Device } from "@capacitor/device";
import { Observable } from "rxjs";
import { AuthService } from "./auth.service";

@Injectable()
export class AppResolverService implements CanActivate {

	constructor(
		private authService: AuthService,
		private http: HttpClient
	) {}

	canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
		return this.initApp();
	}

	initApp(): Promise<boolean> {
		return new Promise<boolean>(async resolve => {
			let deviceInfo = await Device.getInfo();
			let deviceId = await Device.getId();
			const appDevice = {
				appDeviceId: 0,
				deviceId: 0,
				device: {
					platform: deviceInfo.platform,
					model: deviceInfo.model,
					version: deviceInfo.osVersion,
					uuid: deviceId.uuid
				}
			};
			this.registerAppDevice('jesper.integration', appDevice).subscribe(res => {
				this.authService.token = res.app.accessToken;
				resolve(true);
			}, err => {
				resolve(false);
			});
		});
	}

	registerAppDevice(externalAppId: string, appDevice: any): Observable<any> {
		return this.http.post('https://applets-api.colixsystems.com/api/v2/appdevices/register?externalAppId=' + externalAppId, appDevice);
	}
}
