package org.openmrs.module.pihcore.deploy.bundle.sierraLeone;

import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.module.coreapps.CoreAppsConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.openmrs.module.pihcore.deploy.bundle.core.PihCoreMetadataBundle;
import org.openmrs.module.pihcore.metadata.core.Locations;
import org.openmrs.module.pihcore.metadata.mexico.MexicoEncounterTypes;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Requires({ PihCoreMetadataBundle.class,
        SierraLeonePatientIdentifierTypeBundle.class,
        SierraLeoneLocationsBundle.class,
        SierraLeoneAddressBundle.class,
        SierraLeoneEncounterTypeBundle.class } )
public class SierraLeoneMetadataBundle extends AbstractMetadataBundle {

    public static final String DEFAULT_LOCALE = "en";
    public static final String ALLOWED_LOCALES = "en";

    @Override
    public void install() throws Exception {

        log.info("Setting Global Properties");

        Map<String, String> properties = new LinkedHashMap<String, String>();

        // OpenMRS Core
        // (we have to do this rigamarole because of new validations in 2.x that confirms that the allowed list contains the default locale, making it a two-step process to change)
        // (this is also a direct copy of code in LiberiaMetadataBundle and HaitiMetadataBundle, we should abstract this out)
        if (ALLOWED_LOCALES.contains(LocaleUtility.getDefaultLocale().toString())) {
            properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);
        }
        else {
            properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES +", " + LocaleUtility.getDefaultLocale().toString());
        }
        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_DEFAULT_LOCALE, DEFAULT_LOCALE);
        setGlobalProperties(properties);

        properties.put(OpenmrsConstants.GLOBAL_PROPERTY_LOCALE_ALLOWED_LIST, ALLOWED_LOCALES);

        // Core Apps
        properties.put(CoreAppsConstants.GP_DEFAULT_PATIENT_IDENTIFIER_LOCATION, Locations.UNKNOWN.uuid());

        // Order Entry OWA
        properties.put("orderentryowa.labOrderablesConceptSet","517d25f7-2e68-4da4-912b-76090fbfe0fd");

        setGlobalProperties(properties);

        uninstall(possible(GlobalProperty.class, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE), "replaced by metadata mapping");

        // uninstall Mexico encounter type that was originally installed globally
        uninstall(possible(EncounterType.class, MexicoEncounterTypes.MEXICO_CONSULT.uuid()), "now installed only in Mexico");
    }
}
