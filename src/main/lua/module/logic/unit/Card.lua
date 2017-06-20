local unit_card = {}


-- //// debug purpose only \\\\

function unit_card.debug(frame)
    return unit_card.build_unit('Wood','RatWood')
end
function unit_card.debug2(frame)
    return unit_card.build_unit2('Wood','RatWood')
end

function unit_card.debug_build_unit(frame) 
return frame:preprocess(
[==[
<div id="startabber_lang2">
<tabber>
English={{#invoke:Logic/Units/Card | build_card_for_unit|Fire/AngelFire}}
|-|Français={{#invoke:Logic/Units/Card | build_card_for_unit|Light/AngelLight}}
|-|Deutsh={{#invoke:Logic/Units/Card | build_card_for_unit|Shadow/AngelShadow}}
|-|Italiano={{#invoke:Logic/Units/Card | build_card_for_unit|Water/AngelWater}}
|-|Spanish={{#invoke:Logic/Units/Card | build_card_for_unit|Wood/AngelWood}}
</tabber>
</div>

]==]
)
end

-- //// config and utility helpers \\\\

-- ////  \\\\
function unit_card.get_dungeon_data(dungeon_code) 
    local dungeons = mw.loadData("Module:Data/Dungeons")
    return dungeons[dungeon_code]
end

function unit_card.get_color_by_element(element_name)
    local config_data = mw.loadData("Module:Logic/Config")
    return config_data["color_by_element"][element_name];
end

function unit_card.get_softcolor_by_element(element_name)
    local config_data = mw.loadData("Module:Logic/Config")
    return config_data["color_by_element"][element_name.."_soft"];
end

function unit_card.get_box_icon_by_type(box_type)
    local config_data = mw.loadData("Module:Logic/Config")
    return config_data["box_icon_by_type"][box_type];
end 

function unit_card.get_rarity_abrev(rarity)
    local config_data = mw.loadData("Module:Logic/Config")
    local rare = config_data["rarity_table"][rarity] or ""
    return rare
end
function unit_card.get_flag(language, resolution)
    local config_data = mw.loadData("Module:Logic/Config")
    local lan= language
    if resolution and string.len(resolution) > 0 then
        lan = lan .. '_' .. resolution
    end
    
    return config_data["flags_by_lang"][lan];
end

function unit_card.load_unit_data(element_name,unit_code)
    local elt_data = mw.loadData("Module:Data/Units/" .. element_name )
    return elt_data[unit_code];
end


function unit_card.translate(category,wording,language)
    local trans = require("Module:Logic/Config/Translations")
    return trans.find_translations(category,wording,language)
end

-- 


-- the current minicard function
function unit_card.minicard(frame)
    local t = mw.text.split(frame.args[1],"/")
    local element_name = t[1]
    local unit_code = t[2]
    local unit_data = unit_card.load_unit_data(element_name,unit_code)
    local unit_number = unit_data["bestiary"]
    local unit_maxdp            = unit_data["maxdp"]
    local unit_maxhp            = unit_data["maxhp"]
    local unit_initiative       = unit_data["initiative"]
    local unit_skillcharge      = unit_data["skillcharge"]
    local unit_skillname        = unit_data["skillname"]
    local unit_leaderskillname  = unit_data["leaderskillname"]
    local unit_power            = unit_data["power"]

    if unit_skillname and string.len(unit_skillname) == 0 then
        unit_skillname = '-'
    end
    if unit_leaderskillname and string.len(unit_leaderskillname) == 0 then
        unit_leaderskillname = '-'
    end
    
    local disp = {}
    table.insert(disp,'{| border="0" cellpadding="5" cellspacing="1" class="roundborder" width="500px"')
    table.insert(disp,"|-")
    table.insert(disp,'|  style="font-weight:bolder;text-align:center;background-color:' .. unit_card.get_softcolor_by_element(element_name) .. '"  class="roundborder" width=60 | #' .. unit_number )
    table.insert(disp,'| width=70 | [[File:Unit_' .. unit_code .. '_card.png|64x64px|link=Card/'.. element_name .. '/'.. unit_code ..'|center]] ')
    table.insert(disp,'| style="font-weight:bolder" | '.. unit_card.build_unit_link_notable_all_langs(element_name,unit_code))
    table.insert(disp,'|style="width:65px"|[[File:Muscles-icon.png|16px|link=]]&nbsp;' .. unit_power .. 
                        '<br/>[[File:HpGain.png|16px|link=]]&nbsp;' .. unit_maxhp.. 
                        '<br/>[[File:AttackBuff.png|16px|link=]]&nbsp;' .. unit_maxdp.. 
                        '<br>[[File:Lightning.png|16px|link=]]&nbsp;' .. unit_initiative.. 
                        '<br/>[[File:Fusion_icon.png|16px|link=]]&nbsp;' .. unit_skillcharge)
    table.insert(disp,'|  style="font-weight:bolder;background-color:' .. unit_card.get_softcolor_by_element(element_name) .. '"  class="roundborder" width=20| &nbsp;&nbsp;')
    table.insert(disp,"|}")
    return table.concat(disp, "\n")
end


-- ////  \\\\



function unit_card.build_card()
    local title_obj = mw.title.getCurrentTitle()
    
    local t = mw.text.split(title_obj["text"],"/")
    local element_name = t[2]
    local unit_code = t[3]
    return unit_card.build_unit(element_name,unit_code)
end


function unit_card.build_card_for_unit(frame)
    local t = mw.text.split(frame.args[1],"/")
    local element_name = t[1]
    local unit_code = t[2]
    return frame:preprocess(unit_card.build_unit(element_name,unit_code))
end

function unit_card.build_translated_card_for_unit(frame)
    local t = mw.text.split(frame.args[1],"/")
    local size=table.getn(t)
    local element_name = t[size-1]
    local unit_code = t[size]
    local unit_data = unit_card.load_unit_data(element_name,unit_code)
    local lang = frame.args[2] or "en"
    return unit_card.build_unit_translated(unit_data,lang)
end


function unit_card.build_card_for_unit(element_name,unit_code,language)
    local unit_data = unit_card.load_unit_data(element_name,unit_code)
    return unit_card.build_unit_translated(unit_data,language)
end
function unit_card.build_unit(element_name,unit_code) 
    local disp = {}
    table.insert(disp,"{{NB}}")
    table.insert(disp,"<br/>")
    table.insert(disp,'<div id="startabber_lang2"> ')
    table.insert(disp,'<div class="tabcontainer">')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','en')..'</div>{{#invoke:Logic/Units/Card | build_translated_card_for_unit|'.. element_name..'/'.. unit_code..'|en}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','fr')..'</div>{{#invoke:Logic/Units/Card | build_translated_card_for_unit|'.. element_name..'/'.. unit_code..'|fr}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','de')..'</div>{{#invoke:Logic/Units/Card | build_translated_card_for_unit|'.. element_name..'/'.. unit_code..'|de}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','it')..'</div>{{#invoke:Logic/Units/Card | build_translated_card_for_unit|'.. element_name..'/'.. unit_code..'|it}}</div>')
    table.insert(disp,'<div class="tabbox"><div class="tab">' ..  unit_card.translate('common','headers_tab','es')..'</div>{{#invoke:Logic/Units/Card | build_translated_card_for_unit|'.. element_name..'/'.. unit_code..'|es}}</div>')
    table.insert(disp,'</div>')
    table.insert(disp,'</div>')
    table.insert(disp,"<br/>")
    table.insert(disp,"{{NB}}")
    local frame = mw.getCurrentFrame()
    return frame:preprocess(table.concat(disp, "\n"))
end


function unit_card.build_unit_translated(unit_data,lang) 
    local language = lang or "en"
    local unit_number           = unit_data["bestiary"]
    local unit_code             = unit_data["codename"]
    local unit_name             = unit_data["name"..language]
    local unit_maxdp            = unit_data["maxdp"]
    local unit_mindp            = unit_data["mindp"]
    local unit_maxhp            = unit_data["maxhp"]
    local unit_minhp            = unit_data["minhp"]
    local unit_initiative       = unit_data["initiative"]
    local unit_skillcharge      = unit_data["skillcharge"]
    local unit_element          = unit_data["element"]
    local unit_rarity           = unit_data["rarity"]
    local unit_power            = unit_data["power"]
    local unit_skillname        = unit_data["skillname"]
    local unit_leaderskillname  = unit_data["leaderskillname"]
    local unit_maxlevel         = unit_data["maxlevel"]
    local unit_maxxp            = unit_data["maxxp"]
    
    local align_right='! scope="row" style="text-align:right;" '
    local disp = {}
    table.insert(disp,"<center>")
    table.insert(disp,'{| border="1" cellpadding="5" cellspacing="0" class="article-table " width="80%"')
    table.insert(disp,'|-')
    table.insert(disp,'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| #' .. unit_number  .. " - " .. unit_code .. "&nbsp;&nbsp;"..'[[File:'..  unit_card.get_flag(language) .. '|link=]]' .. "&nbsp;" ..  unit_name )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. '  | '.. unit_card.translate("unit_card","icon",language) )
    table.insert(disp,'| style="text-align:center;vertical-align:center" |[[File:Unit_' .. unit_code .. '_card.png|thumb|center|link=]]')
    table.insert(disp,align_right .. '  | '.. unit_card.translate("unit_card","visual",language) )
    table.insert(disp,'| style="text-align:center;vertical-align:center" |[[File:Unit_' .. unit_code .. '_sprite.png|thumb|center|link=]]')
    table.insert(disp,'|-')
    table.insert(disp,'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| '.. unit_card.translate("unit_card","statistics",language) )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","power",language) )
    table.insert(disp,'|  colspan=2|' .. unit_power )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","max_level",language) )
    table.insert(disp,'|  colspan=2|' .. unit_maxlevel .. ' ( ' .. unit_maxxp .. ' xp)')
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","health_min_max",language) )
    table.insert(disp,'|  colspan=2|' .. unit_minhp .. ' / ' .. unit_maxhp )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","damages_min_max",language) )
    table.insert(disp,'|  colspan=2|' .. unit_mindp .. ' / ' .. unit_maxdp )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","skill_charge",language) )
    table.insert(disp,'|  colspan=2|' .. unit_skillcharge )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","initiative",language) )
    table.insert(disp,'|  colspan=2|' .. unit_initiative )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","element",language) )
    table.insert(disp,'|  colspan=2|' .. unit_element )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. ' colspan=2| '.. unit_card.translate("unit_card","rarity",language) )
    table.insert(disp,'|  colspan=2|' .. unit_card.get_rarity_abrev(unit_rarity) )
    table.insert(disp,'|-')
    table.insert(disp,'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| '.. unit_card.translate("unit_card","skills",language) )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. '| '.. unit_card.translate("unit_card","basic_skill",language) )
    table.insert(disp,unit_card.build_skill_cell(unit_code,unit_skillname, "Basic",language) )
    table.insert(disp,'|-')
    table.insert(disp,align_right .. '| '.. unit_card.translate("unit_card","leader_skill",language))
    table.insert(disp,unit_card.build_skill_cell(unit_code,unit_leaderskillname, "Leader",language))
    table.insert(disp,unit_card.add_morphed_into(unit_data,language))
    table.insert(disp,unit_card.add_morphed_from(unit_data,language))
    table.insert(disp,unit_card.add_used_by(unit_data,language))
    table.insert(disp,unit_card.add_found_in_dungeons(unit_data,language))
    table.insert(disp,'|}') 
    table.insert(disp,unit_card.build_categories(unit_data))
    table.insert(disp,"</center>")

    return table.concat(disp,'\n')

end

function unit_card.add_found_in_dungeons(unit_data,language)
    local unit_element = unit_data["element"]
    local foundin = unit_data["foundin"]
    local disp = '|-\n' ..
        '! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| ' .. unit_card.translate("unit_card","found_in",language) .. '\n' ..
        '|-\n' ..
        '| colspan="4" style="text-align:center" | \n'

    local notfounded = true
    for k,v in pairs(foundin) do
        if notfounded then
            disp = disp .. '{| border="0" cellpadding="5" cellspacing="0" class="article-table"  style="width:100%"\n'
            disp = disp .. '|-\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","chapter",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","campaign",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","dungeon",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","mode",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","name",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","box_type",language) .. '\n'
            disp = disp .. '! scope="col"  style="text-align:center"| ' .. unit_card.translate("unit_card","level_of_unit",language) .. '\n'

            notfounded = false 
        end
        
        local t = mw.text.split(v,"@")
        local dungeon_code = t[1]
        local box_type = t[2]
        local level = t[3]
        local mode = t[4] or "normal"
        local dung_data = unit_card.get_dungeon_data(dungeon_code) 
        disp = disp .. '|-\n'
        disp = disp .. '| style="text-align:center; font-weight:bold" | ' .. dung_data["chapter"] .. '\n'
        disp = disp .. '| style="text-align:center; font-weight:bold" | ' .. dung_data["campaign"] .. '\n'
        disp = disp .. '| style="text-align:center; font-weight:bold" | ' .. dung_data["dungeon"] .. '\n'
        disp = disp .. '| style="text-align:center; font-weight:bold" | ' .. unit_card.translate("unit_card","mode_"..mode,language) .. '\n'
        disp = disp .. '| style="text-align:center; font-weight:bolder" | ' .. dung_data["name"] .. '\n'
        disp = disp .. '| [[File:' .. unit_card.get_box_icon_by_type(box_type) .. '|thumb|32px|link=|center]]\n'
        disp = disp .. '| style="text-align:center; font-weight:bolder" | ' .. level .. '\n'
        
    end
    if notfounded then
        disp = disp .. ' <b><i>' .. unit_card.translate("unit_card","none_found_in",language) .. '</i></b>\n'
    else
         disp = disp .. '|}\n'
    end
    
   
    return disp  
end

function unit_card.add_used_by(unit_data,language)
    local unit_element = unit_data["element"]
    local disp = '|-\n' ..
'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| ' .. unit_card.translate("unit_card","used_by",language) .. '\n'
    local users = unit_data["usedby"]
    local opentd = true
    local one_use = false
    for k,v in pairs(users) do
        one_use=true;
        if opentd then 
            disp = disp .. '|-\n' 
            opentd = false
        else
            opentd = true
        end
        disp = disp ..  '|  style="text-align:center;"  colspan="2"|   ' .. unit_card.add_material_card(unit_element,v) .. '<br/>'..unit_card.build_unit_link_notable(unit_element,v,language)..'\n'
    end
    if not opentd then
        disp = disp ..  '|  style="text-align:center;"  colspan="2"|  &nbsp;\n'
    end
    if not one_use then
        disp = disp .. '|-\n' 
        disp = disp ..  '|  style="text-align:center;"  colspan="4"|  <b><i>' .. unit_card.translate("unit_card","none_wording",language) .. '</i></b>\n'
    end
    
    
    return disp 
end

function unit_card.add_morphed_from(unit_data,language)
    local unit_element      = unit_data["element"]
    local unit_morphfrom    = unit_data["morphsfrom"]
    
    
    local disp = '|-\n' ..
'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| ' .. unit_card.translate("unit_card","metamorphosed_from",language) .. '\n' .. '|-\n'
    if unit_morphfrom and string.len(unit_morphfrom) > 0 then
        local t = mw.text.split(unit_morphfrom,"/")
        local from_elt = t[1]
        local from_code = t[2]
        disp = disp .. '| colspan=2 style="text-align:center;" | [[File:Unit_' .. from_code .. '_sprite.png|thumb|center|link=Card/'..from_elt..'/'..from_code ..']]\n' ..
        '| colspan=2 style="text-align:center;" | <b><center>' .. unit_card.build_unit_link(from_elt,from_code,language)..'</center></b>\n' 
    else
        disp = disp ..'| colspan="4" style="text-align:center" | <b><i>' .. unit_card.translate("unit_card","none_wording",language) .. '</i></b>\n'
    end
    return disp 
end

function unit_card.add_morphed_into(unit_data,language)
    local unit_element = unit_data["element"]
    local disp = '|-\n' ..
'! colspan="4" style="text-align:center;background-color:'.. unit_card.get_color_by_element(unit_element) ..'"| ' .. unit_card.translate("unit_card","metamorphosis",language) .. '\n' ..
'|-\n' ..
unit_card.build_metamorphose(unit_data,language) 
    return disp 
end


function unit_card.build_skill_code(skill_name)
    local data = string.gsub(skill_name," ","_")
    data = string.gsub(data,"%-","_")
    return string.gsub(data,"'","_")
end

function unit_card.build_skill_cell(unit_code,skill_name, skills_data_type,language)
    local skill_code;
    local disp = '| colspan=3 |'
    if skill_name and string.len(skill_name) > 0 then
        local skills_data = mw.loadData("Module:Data/Skills/" .. skills_data_type)
        skill_code = unit_card.build_skill_code(skill_name)
 
        if skill_code  and string.len(skill_code) > 0 then
            local t = skills_data[skill_code]
            if t then
                  disp = disp  .. '<u>[[Skill/' .. skills_data_type ..'/'.. skill_code ..'|'..  t["names"][language] .. ']]</u><br /><i>' .. t["descriptions"][language].. '</i>\n'
            else
                disp = disp .. ' erratic skill code : '.. skill_code .. '\n'
            end
        else
             disp = disp .. ' erratic skill code : '.. skill_code .. '\n'
        end
    else
        disp = disp .. ' - \n'
    end
    return disp
 
end


function unit_card.build_unit_link_all_langs(unit_element,unit_code)
    local link = ''
    local data =unit_card.load_unit_data(unit_element,unit_code)
    link = '<table border=0 cellpadding=0 cellspacing=0 >' ..
    '<tr><td>[[File:'..  unit_card.get_flag('en','16') .. '|right|link=]]</td><td>' ..'[[Card/' .. unit_element .. '/' .. unit_code .. '|' ..  data['nameen'] ..']]</td></tr>'
        .. '<tr><td>[[File:'..  unit_card.get_flag('fr','16') .. '|right|16x16px|link=]]</td><td>' .. data['namefr'] ..'</td></tr>'
        .. '<tr><td>[[File:'..  unit_card.get_flag('it','16') .. '|right|link=]]</td><td>' .. data['nameit'] ..'</td></tr>'
        .. '<tr><td>[[File:'..  unit_card.get_flag('es','16') .. '|right|link=]]</td><td>' .. data['namees'] ..'</td></tr>'
        .. '<tr><td>[[File:'..  unit_card.get_flag('de','16') .. '|right|link=]]</td><td>' .. data['namede']..'</td></tr></table>'
    return link
end

function unit_card.build_unit_link_notable_all_langs(unit_element,unit_code)
    local link = ''
    local element_name = unit_element
    local code = unit_code
    if string.find(code,'/',1,true) then
        local t = mw.text.split(code,"/")
        element_name = t[1]
        code = t[2]
    end
    
    
    local data =unit_card.load_unit_data(element_name,code)
    link = '[[File:'..  unit_card.get_flag('en','16') .. '|link=]]&nbsp;[[Card/' .. element_name .. '/' .. code .. '|' ..  data['nameen'] ..']]<br/>'
        .. '[[File:'..  unit_card.get_flag('fr','16') .. '|link=]]&nbsp;' .. data['namefr'] ..'<br/>'
        .. '[[File:'..  unit_card.get_flag('it','16') .. '|link=]]&nbsp;' .. data['nameit'] ..'<br/>'
        .. '[[File:'..  unit_card.get_flag('es','16') .. '|link=]]&nbsp;' .. data['namees'] ..'<br/>'
        .. '[[File:'..  unit_card.get_flag('de','16') .. '|link=]]&nbsp;' .. data['namede']..'<br/>'
    return link
end


function unit_card.build_unit_link(unit_element,unit_code,language)
    local link = ''
    local data =unit_card.load_unit_data(unit_element,unit_code)
    link = '<table border=0 cellpadding=0 cellspacing=0 >' ..
    '<tr><td>[[File:'..  unit_card.get_flag(language,'16') .. '|right|link=]]</td><td>' ..'[[Card/' .. unit_element .. '/' .. unit_code .. '|' ..  data['name'..language] ..']]</td></tr></table>'
    return link
end

function unit_card.build_unit_link_notable(unit_element,unit_code,language)
    local link = ''
    local element_name = unit_element
    local code = unit_code
    if string.find(code,'/',1,true) then
        local t = mw.text.split(code,"/")
        element_name = t[1]
        code = t[2]
    end
    
    
    local data =unit_card.load_unit_data(element_name,code)
    link = '[[File:'..  unit_card.get_flag(language,'16') .. '|link=]]&nbsp;[[Card/' .. element_name .. '/' .. code .. '|' ..  data['name'.. language ] ..']]'
    return link
end


function unit_card.add_material_card(mat_element,material) 
    local code = material
    local element = mat_element
    if string.find(material,'/',1,true) then
        local t = mw.text.split(material,"/")
        element = t[1]
        code = t[2]
    end
    return '[[File:Unit_' .. code .. '_card.png|thumb|64px|center|link=Card/'..element .. '/' .. code .. ']]'
end

function unit_card.build_metamorphose(unit_data,language)
    local unit_element          = unit_data["element"]
    local unit_morphinto        = unit_data["morphsinto"]
    local unit_material1        = unit_data["material1"]
    local unit_material2        = unit_data["material2"]
    local unit_material3        = unit_data["material3"]
    local morph_data = unit_card.load_unit_data(unit_element,unit_morphinto)
    local disp = "";
    if unit_material1 and string.len(unit_material1) > 0 then
        disp = '! scope="row" style="text-align:right;"  | ' .. unit_card.translate("unit_card","material",language) .. ' 1\n' ..
        '|  style="text-align:center;"  |   ' .. unit_card.add_material_card(unit_element,unit_material1) .. '<br/>'..unit_card.build_unit_link_notable(unit_element,unit_material1,language)..'\n' ..
        '| colspan=2 rowspan=3  style="text-align:center;" | [[File:Unit_' .. unit_morphinto .. '_sprite.png|thumb|center|link=Card/'..unit_element..'/'..unit_morphinto ..']]<br /><b><center>' .. unit_card.build_unit_link(unit_element,unit_morphinto,language)..'</center></b>\n' ..
        '|-\n' ..
        '! scope="row" style="text-align:right;" |  ' .. unit_card.translate("unit_card","material",language) .. ' 2\n' ..
        '| style="text-align:center;"  | ' .. unit_card.add_material_card(unit_element,unit_material2) .. '<br/>'..unit_card.build_unit_link_notable(unit_element,unit_material2,language)..'\n' ..
        '|-\n' ..
        '! scope="row" style="text-align:right;"   | ' .. unit_card.translate("unit_card","material",language) .. ' 3\n'
        if unit_material3 and string.len(unit_material3) > 0 then
            disp = disp .. '|  style="text-align:center;"  |  ' .. unit_card.add_material_card(unit_element,unit_material3) .. '<br/>'..unit_card.build_unit_link_notable(unit_element,unit_material3,language)..' \n' 
        else
            disp = disp .. '|  style="text-align:center;"| -\n' 
        end
        return disp
    else
        return '| colspan="4" style="text-align:center" | <b><i>' .. unit_card.translate("unit_card","none_wording",language) .. '</i></b>\n' 
    end
end

function unit_card.build_categories(unit_data)
    local cats = ""
    local code = unit_data["codename"]
--    cats =unit_card.insert_data(cats,code)
--    cats =unit_card.insert_data(cats,unit_data["nameen"])
--    cats =unit_card.insert_data(cats,unit_data["namefr"])
--    cats =unit_card.insert_data(cats,unit_data["nameit"])
--    cats =unit_card.insert_data(cats,unit_data["namees"])
--    cats =unit_card.insert_data(cats,unit_data["namede"])
    cats =unit_card.insert_data(cats,unit_data["element"].."_"..unit_data["rarity"])
--    cats =unit_card.insert_data(cats,unit_data["skillname"])
--    cats =unit_card.insert_data(cats,unit_data["leaderskillname"])
--    cats =unit_card.insert_data(cats,unit_data["morphsinto"])
--    cats =unit_card.insert_data(cats,unit_data["material1"])
--    cats =unit_card.insert_data(cats,unit_data["material2"])
--    cats =unit_card.insert_data(cats,unit_data["material3"])
    return cats

end

function unit_card.insert_data(cats, data)
    if data and string.len(data) > 0  then
        cats = cats.."[[Category:" .. data .. "]]"
    end
    return cats
end


function unit_card.insert_data_code(cats, data, code)
    if data and string.len(data) > 0  then
        cats = cats.."[[Category:" .. data .. "|" .. code .. "]]"
    end
    return cats
end
return unit_card
