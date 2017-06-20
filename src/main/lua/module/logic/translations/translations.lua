--[[

Translations referential.

if one language is missing, default fallback will be English(en).

--]]
translations = {}
translations.common={
	headers_tab = {
		en="English",
		fr="Français",
		es="Español",
		de="Deutsch",
		it="Italiano"
	}
}
translations.unit_card = {
	icon={
		en="Bestiary Icon",
		fr="Icône du bestiaire",
	},
	visual= {
		en="Card visual",
		fr="Visuel de la carte",
	},
	statistics= {
		en="Statistics",
		fr="Statistiques",
	},
	power={
		en="Power",
		fr="Puissance",
		it="Energia",
	},
	max_level={
		en="max level",
		fr="Niveau Maximum",
	},
	health_min_max={
		en="Health min / max",
		fr="Santé min / max",
	},
	damages_min_max={
		en="damages min / max",
		fr="Dégâts min / max",
	},
	skill_charge={
		en="skill charge",
		fr="Charges de pouvoir",
		it="Carica potere",
	},
	initiative={
		en="Initiative",
		fr="Initiative",
		it="Iniziativa",
	},
	element={
		en="Element",
		fr="Elément",
		it="Elemento",
	},
	rarity={
		en="Rarity",
		fr="Rareté",
		it="Rarità",
	},
	skills={
		en="Skills",
		fr="Pouvoirs",
	},
	basic_skill={
		en="Basic",
		fr="Pouvoir",
		it="Potere",
	},
	leader_skill={
		en="Leader",
		fr="Super pouvoir",
		it="Dote Guida",
	},
	metamorphosis={
		en="Metamorphose",
		fr="Evolution",
		it="Progessione",
	},
	material={
		en="Material",
		fr="Composant",
	},
	metamorphosed_from={
		en="Metamorphosed from",
		fr="Evoluée depuis",
	},
	none_wording={
		en="None",
		fr="Aucune",
	},
	used_by={
		en="Used by following(s) metamorphose(s)",
		fr="Utilisée dans les évolutions suivantes",
	},
	found_in={
		en="Found in following(s) dungeon(s)",
		fr="Trouvée dans les donjons suivants",
	},
	none_found_in={
		en="Summon(Diamonds / Crystal / Ally rings) or Special events or Metamorphosis",
		fr="Invocation(Diamonds / Crystaux / Anneaux d'alliés) ou Evènements ou Evolution",
	},
	chapter={
		en="Chapter",
		fr="Chapitre",
	},
	campaign={
		en="Campaign",
		fr="Campagne",
	},
	dungeon={
		en="Dungeon",
		fr="Donjon",
	},
	mode={
		en="Mode",
		fr="Mode",
	},
	mode_normal={
		en="Normal",
		fr="Normal",
	},
	mode_elite={
		en="Elite",
		fr="Elite",
	},
	name={
		en="Name",
		fr="Nom",
		it="Nome",
	},
	box_type={
		en="Box type",
		fr="Type de Boîte",
	},
	level_of_unit={
		en="Level of unit",
		fr="Niveau de l'unité",
	}
}

translations.unit_listing = {}

translations.skill={
    units_using ={
        en="Unit(s) using this skill",
        fr="Unité(s) utilisant ce pouvoir",
    }
}


function translations.find_translations(category, name, lang) 
--[[
find a translation by category then name then language.
    If category or name is missing, "No wording found" returned
    If language is missing, english one returned instead.
--]]

    local cat = translations[category]
    local trans = "No wording found"
    if cat then
        local l = cat[name]
        if l then
            trans = l[lang]
            if  not trans or string.len(trans) < 1 then
                trans = l["en"] or ""
            end 
        end
    end
    return trans
end

return translations
