NOTE: This plugin was previously called: EASE Plugin (ease-plugin).



| Plugin Information                                                                                                          |
|-----------------------------------------------------------------------------------------------------------------------------|
| View Digital.ai App Management Publisher [on the plugin site](https://plugins.jenkins.io/ease-plugin) for more information. |

Older versions of this plugin may not be safe to use. Please review the
following warnings before using an older version:

-   [Credentials stored in plain
    text](https://jenkins.io/security/advisory/2019-02-19/#SECURITY-1070)

This plugin adds the ability to publish updates of mobile apps to
[Apperian](https://www.arxan.com/apperian) App Management as part of the
Jenkins build process.

# Usage

When you install this plugin, you will have the ability to add the
"Arxan MAM Publisher" as a post-build action on a project's Configure
page.

After adding the action to your project, configure it to specify the
values that Jenkins will use to publish the app to Apperian during the
build process. For more information, see [Jenkins
Integration](https://help.apperian.com/x/DQBT).

# Version History

Version 2.2 (2019-03-6)

-   Correctly check user permission when choosing API Token

Version 2.1 (2019-02-14)

-   Fixed a bug where publishing would fail if the job is running on a
    slave node.

Version 2.0 (2018-11-13)

-   Plugin renamed from EASE Plugin to Arxan MAM Publisher.
-   Build Step renamed from 'Apperian Plugin' to 'Arxan MAM Publisher'.
-   Removed PHP API URL for 'Custom URLs' environment.
-   Changed from using Username/Password to using API Token for
    Authentication. For more information, see [Manage API
    Tokens](https://help.apperian.com/x/tYWI).
-   Added Application Name, Short Description, and Long Description as
    editable fields.
-   Binary Metadata is now updated after uploading a new app version.
-   Added the ability to reapply policies when updating an app.
-   Added Pipeline support.

Version 1.2.6 (2015-05-17)

-   Added possibility to update metadata and extract it from Android,
    iOS, Blackberry and other file types.

Version 1.2.3 (2014-04-21)

-   Initial release.
