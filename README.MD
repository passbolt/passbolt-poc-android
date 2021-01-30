	      ____                  __          ____
	     / __ \____  _____ ____/ /_  ____  / / /_
	    / /_/ / __ `/ ___/ ___/ __ \/ __ \/ / __/
	   / ____/ /_/ (__  |__  ) /_/ / /_/ / / /_
	  /_/    \__,_/____/____/_.___/\____/_/\__/

	Open source password manager for teams
	(c) 2021 Passbolt SA
	https://www.passbolt.com

# About this project

This project contains preliminary work for the development of Passbolt mobile application proof of
concept aka POC. It does not contain a functional mobile application yet. 

Passbolt POC project requires Android API **version >= than 30 (Android 11)**. You can run it on a compatible device or an emulator.
IDE used for development is Android Studio 4.2 with installed SDK platform 11 and latest build tools.

This is a proof of concept so there is no architecture, DI etc.

The app verifies certificates of the apps that request AutoFill. For the purpose of this POC apps that can be AutoFilled are:
 - Passbolt POC AutoFill tests
 - Instagram (with POC for App Link verification)
 - Facebook
 - Mobile websites opened in Google Chrome for Android

## License

Passbolt - Open source password manager for teams

(c) 2021 Passbolt SA

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
Public License (AGPL) as published by the Free Software Foundation version 3.

The name "Passbolt" is a registered trademark of Passbolt SA, and Passbolt SA hereby declines to grant a trademark
license to "Passbolt" pursuant to the GNU Affero General Public License version 3 Section 7(e), without a separate
agreement with Passbolt SA.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with this program. If not,
see [GNU Affero General Public License v3](http://www.gnu.org/licenses/agpl-3.0.html).
