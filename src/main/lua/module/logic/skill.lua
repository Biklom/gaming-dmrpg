local skills = {}

local unit_card = require('Module:Logic/Units/Card')
local unit_listing = require('Module:Logic/Units/Listing')

function skills.debug()
    return skills.display_full('Basic','Absolution')
end


function skills.load_skill_data(skill_type,skill_code)
    local elt_data = mw.loadData("Module:Data/Skills/" .. skill_type )
    return elt_data[skill_code];
end

function skills.translate(category,wording,language)
    local trans = require("Module:Logic/Config/Translations")
    return trans.find_translations(category,wording,language)
end

function skills.display_it()
    return skills.display_it2()
end

function skills.display_it_old_full()
    local title_obj = mw.title.getCurrentTitle()
    local t = mw.text.split(title_obj["text"],"/")
    local skill_type = t[2]
    local skill_code = t[3]
    
    return skills.display_full(skill_type,skill_code)
end


function skills.display_it2()
    local title_obj = mw.title.getCurrentTitle()
    local t = mw.text.split(title_obj["text"],"/")
    local skill_type = t[2]
    local skill_code = t[3]
    local disp = {}
    table.insert(disp,"{{NB}}")
    table.insert(disp,"<br/>")
    table.insert(disp,'<div id="startabber_lang2"> ')
    table.insert(disp,'<div class="tabcontainer">')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','en')..'</div>{{#invoke:Logic/Skills | display_it_translated|'.. skill_type..'/'.. skill_code..'|en}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','fr')..'</div>{{#invoke:Logic/Skills | display_it_translated|'.. skill_type..'/'.. skill_code..'|fr}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','de')..'</div>{{#invoke:Logic/Skills | display_it_translated|'.. skill_type..'/'.. skill_code..'|de}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','it')..'</div>{{#invoke:Logic/Skills | display_it_translated|'.. skill_type..'/'.. skill_code..'|it}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','es')..'</div>{{#invoke:Logic/Skills | display_it_translated|'.. skill_type..'/'.. skill_code..'|es}}</div>')
    table.insert(disp,'</div>')
    table.insert(disp,'</div>')
    table.insert(disp,"<br/>")
    table.insert(disp,"{{NB}}")
 
    local frame = mw.getCurrentFrame()
    return frame:preprocess(table.concat(disp, "\n"))
end

function skills.display_it_translated(frame)
    local t = mw.text.split(frame.args[1],"/")
    local size=table.getn(t)
    local skill_type = t[size-1]
    local skill_code = t[size]
    local lang = frame.args[2] or "en"
    return skills.display_translated_full(skill_type,skill_code,lang)
end

function skills.display_as_list()
    local title_obj = mw.title.getCurrentTitle()
    local t = mw.text.split(title_obj["text"],"/")
    local skill_type = t[2]
    return skills.display_list(skill_type)
end

function skills.display_full(skill_type,skill_code)
    local skcode = unit_listing.build_skill_code(skill_code)
    local skill_data = skills.load_skill_data(skill_type,skcode)
    local disp = {}
    table.insert(disp,'{| border="1" cellpadding="5" cellspacing="0" class="article-table " width="100%"')
    table.insert(disp,'|-')
    if skill_data then
        table.insert(disp,'! style="text-align:center; font-weight:bolder" colspan="2" | ' .. (skill_data["englishname"]))
        table.insert(disp,'|-')
        table.insert(disp,'| colspan="2" | ' .. (skill_data["description"]))
        table.insert(disp,'|-')
        table.insert(disp,'! colspan="2" style="text-align:center" | Unit(s) using this skill ')
        local users = skill_data["usedby"]
        if users then
            local nouser=true;
            local opentd = true;
            for k,v in pairs(users) do
                nouser=false;
                if opentd then 
                    table.insert(disp,'|-')
                    opentd = false
                else
                    opentd = true
                end
                table.insert(disp,'|  style="text-align:center;width:50%" |   ' .. 
                                unit_card.add_material_card(unit_element,v) .. '<br/>'..
                                unit_card.build_unit_link_notable_all_langs(unit_element,v))
            end
            if not opentd then
                table.insert(disp, '|  style="text-align:center;"|   -')
            end
            if nouser then
                table.insert(disp,'|-')
                table.insert(disp,'| colspan="2" style="text-align:center" | -')
            end
            
        else 
            table.insert(disp,'|-')
            table.insert(disp,'| colspan="2" style="text-align:center" | -')
        end
    else
        table.insert(disp,'! style="text-align:center" | skill_code erratic : ' .. t)
    end
    table.insert(disp,'|}')
    return table.concat(disp, "\n")  
end


function skills.display_translated_full(skill_type,skill_code,language)
    local skcode = unit_listing.build_skill_code(skill_code)
    local skill_data = skills.load_skill_data(skill_type,skcode)
    local disp = {}
    table.insert(disp,'{| border="1" cellpadding="5" cellspacing="0" class="article-table " width="100%"')
    table.insert(disp,'|-')
    if skill_data then
        table.insert(disp,'! style="text-align:center; font-weight:bolder" colspan="2" | ' .. (skill_data["names"][language]))
        table.insert(disp,'|-')
        table.insert(disp,'| colspan="2" | ' .. (skill_data["descriptions"][language]))
        table.insert(disp,'|-')
        table.insert(disp,'! colspan="2" style="text-align:center" | ' .. skills.translate("skill","units_using",language))
        local users = skill_data["usedby"]
        if users then
            local nouser=true;
            local opentd = true;
            for k,v in pairs(users) do
                nouser=false;
                if opentd then 
                    table.insert(disp,'|-')
                    opentd = false
                else
                    opentd = true
                end
                table.insert(disp,'|  style="text-align:center;width:50%" |   ' .. 
                                unit_card.add_material_card(unit_element,v) .. '<br/>'..
                                unit_card.build_unit_link_notable(unit_element,v,language))
            end
            if not opentd then
                table.insert(disp, '|  style="text-align:center;"|   -')
            end
            if nouser then
                table.insert(disp,'|-')
                table.insert(disp,'| colspan="2" style="text-align:center" | -')
            end
            
        else 
            table.insert(disp,'|-')
            table.insert(disp,'| colspan="2" style="text-align:center" | -')
        end
    else
        table.insert(disp,'! style="text-align:center" | skill_code erratic : ' .. t)
    end
    table.insert(disp,'|}')
    return table.concat(disp, "\n")  
end

function skills.display_list(skill_type)
    local skills_data = mw.loadData("Module:Data/Skills/" .. skill_type )
    local disp = {}
    table.insert(disp,'{| border="1" cellpadding="5" cellspacing="0" class="article-table " width="100%"')
    table.insert(disp,'|-')
    table.insert(disp,'!style="text-align:center" colspan="2"|List of '..skill_type..' skills currently ingame')
    for k,v in unit_listing.spairs(skills_data,function(t,a,b) return t[a]["englishname"] < t[b]["englishname"] end) do
        table.insert(disp,'|-')
        table.insert(disp,'| style="font-weight:bolder"|[[Skill/' .. skill_type.. '/' .. k .. '|'.. v["englishname"] .. ']]')
        table.insert(disp,'| ' .. v["description"])
    end
    table.insert(disp,'|}')
    return table.concat(disp, "\n")      
end


return skills
